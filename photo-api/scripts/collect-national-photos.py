#!/usr/bin/env python3
"""Baixa fotos de jogadores COM A CAMISA DA SELECAO, via Wikidata + Wikimedia Commons.

Diferente de collect-photos.mjs (TheSportsDB), que devolve recortes com uniforme
de clube. As fotos do Commons costumam ser de jogos de selecao — boa parte e da
propria Copa 2026.

Casamento em duas etapas, porque so o nome erra: "Wesley" bateu num homonimo e
"Vinicius Junior" nao bateu em "Vinicius Junior".
  1. rotulo exato no Wikidata, filtrando por ocupacao (jogador/tecnico);
  2. quem sobrar, por DATA DE NASCIMENTO (que o football-data fornece) mais
     sobreposicao de palavras do nome — preciso o bastante para separar os dois
     Danilos do Brasil.

Uso:
  FOOTBALL_API_TOKEN=xxx python3 scripts/collect-national-photos.py
  FOOTBALL_API_TOKEN=xxx TEAMS="Brazil,Argentina" python3 scripts/collect-national-photos.py

Requer: Pillow. O endpoint usado e query.wikidata.org (SPARQL); a API
www.wikidata.org/w/api.php costuma responder 429 em IP compartilhado.
"""
import json
import os
import re
import sys
import time
import unicodedata
import urllib.parse
import urllib.request

TOKEN = os.environ.get("FOOTBALL_API_TOKEN")
if not TOKEN:
    sys.exit("Defina FOOTBALL_API_TOKEN (token do football-data.org).")

COMPETITION = os.environ.get("COMPETITION_CODE", "WC")
ONLY = [t.strip() for t in os.environ.get("TEAMS", "").split(",") if t.strip()]
UA = "Copa26AlbumDigital/1.0 (projeto academico UFU)"
ROOT = os.path.join(os.path.dirname(__file__), "..", "photos")
SPARQL = "https://query.wikidata.org/sparql"

try:
    from PIL import Image
except ImportError:
    sys.exit("Pillow nao instalado. Rode: pip install Pillow")


def get(url, headers=None, retries=3):
    for attempt in range(retries + 1):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": UA, **(headers or {})})
            with urllib.request.urlopen(req, timeout=120) as r:
                return r.read()
        except Exception:
            if attempt == retries:
                return None
            time.sleep(2 * 2 ** attempt)


def sparql(query):
    url = SPARQL + "?" + urllib.parse.urlencode({"query": query})
    raw = get(url, {"Accept": "application/sparql-results+json"})
    if not raw:
        return []
    try:
        return json.loads(raw.decode("utf-8", "replace"), strict=False)["results"]["bindings"]
    except Exception:
        return []


def norm(s):
    s = unicodedata.normalize("NFKD", s.lower())
    s = "".join(c for c in s if not unicodedata.combining(c))
    return set(re.sub(r"[^a-z ]", "", s).split())


def squads():
    raw = get(f"https://api.football-data.org/v4/competitions/{COMPETITION}/teams",
              {"X-Auth-Token": TOKEN})
    teams = json.loads(raw)["teams"]
    if ONLY:
        teams = [t for t in teams if t["name"] in ONLY]
    people = []
    for t in teams:
        for p in t.get("squad") or []:
            people.append({"team": t["name"], "id": p["id"], "name": p["name"],
                           "dob": p.get("dateOfBirth"), "role": "players"})
        if t.get("coach"):
            c = t["coach"]
            people.append({"team": t["name"], "id": c["id"], "name": c.get("name", ""),
                           "dob": c.get("dateOfBirth"), "role": "coaches"})
    return people


def by_label(people, batch=40):
    """Etapa 1: rotulo exato + ocupacao."""
    found, names = {}, sorted({p["name"].strip() for p in people})
    for i in range(0, len(names), batch):
        vals = " ".join('"%s"@en' % n.replace('"', "") for n in names[i:i + batch])
        rows = sparql("SELECT ?label ?image WHERE { VALUES ?label { %s } "
                      "VALUES ?occ { wd:Q937857 wd:Q628099 } "
                      "?p rdfs:label ?label ; wdt:P106 ?occ ; wdt:P18 ?image . }" % vals)
        for r in rows:
            found.setdefault(r["label"]["value"].strip(), r["image"]["value"])
        time.sleep(1.5)
    return found


def by_birthdate(pending, batch=25):
    """Etapa 2: data de nascimento + sobreposicao de nome, para homonimos e acentos."""
    found = {}
    dated = [p for p in pending if p.get("dob")]
    for i in range(0, len(dated), batch):
        chunk = dated[i:i + batch]
        vals = " ".join('"%s"^^xsd:dateTime' % p["dob"] for p in chunk)
        rows = sparql('SELECT ?pLabel ?dob ?image WHERE { VALUES ?dob { %s } '
                      '?p wdt:P569 ?dob ; wdt:P106 wd:Q937857 ; wdt:P18 ?image . '
                      'SERVICE wikibase:label { bd:serviceParam wikibase:language "en,pt,es". } }' % vals)
        pool = {}
        for r in rows:
            pool.setdefault(r["dob"]["value"][:10], []).append((r["pLabel"]["value"], r["image"]["value"]))
        for p in chunk:
            alvo, best, score = norm(p["name"]), None, 0
            for label, image in pool.get(p["dob"], []):
                s = len(alvo & norm(label))
                if s > score:
                    score, best = s, image
            if best:
                found[p["id"]] = best
        time.sleep(1.5)
    return found


def square_top(im, bias=0.15):
    """Recorte quadrado puxado para o topo, para o rosto ficar centrado no avatar circular."""
    w, h = im.size
    s = min(w, h)
    if h > w:
        top = int((h - s) * bias)
        return im.crop((0, top, s, top + s))
    left = (w - s) // 2
    return im.crop((left, 0, left + s, s))


def main():
    people = squads()
    print(f"{len(people)} pessoas em {len({p['team'] for p in people})} selecoes")

    porlabel = by_label(people)
    urls = {p["id"]: porlabel[p["name"].strip()] for p in people if p["name"].strip() in porlabel}
    print(f"etapa 1 (rotulo): {len(urls)}")

    faltam = [p for p in people if p["id"] not in urls]
    urls.update(by_birthdate(faltam))
    print(f"etapa 2 (nascimento): {len(urls)} no total")

    for kind in ("players", "coaches"):
        os.makedirs(os.path.join(ROOT, kind), exist_ok=True)
    salvas = 0
    for p in people:
        url = urls.get(p["id"])
        if not url:
            continue
        arquivo = urllib.parse.unquote(url.split("/")[-1])
        raw = get("https://commons.wikimedia.org/wiki/Special:FilePath/"
                  + urllib.parse.quote(arquivo) + "?width=400")
        if not raw or len(raw) < 2000:
            continue
        tmp = os.path.join(ROOT, p["role"], f"{p['id']}.tmp")
        with open(tmp, "wb") as f:
            f.write(raw)
        try:
            im = Image.open(tmp).convert("RGB")
            square_top(im).resize((320, 320), Image.LANCZOS).save(
                os.path.join(ROOT, p["role"], f"{p['id']}.webp"), "WEBP", quality=82, method=6)
            salvas += 1
        finally:
            os.remove(tmp)
        time.sleep(0.45)

    print(f"\nsalvas: {salvas} | sem foto: {len(people) - salvas}")
    print("Confira o resultado: nem toda foto do Commons e com a camisa da selecao "
          "(alguns jogadores so tem foto de clube ou aparecem de colete de aquecimento).")


if __name__ == "__main__":
    main()

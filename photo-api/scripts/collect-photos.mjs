/**
 * Script de coleta de fotos (executa 1x, offline).
 *
 * Lê os elencos da MESMA fonte do app (football-data.org) para garantir que as
 * fotos fiquem indexadas pelo id de jogador/técnico usado pelo aplicativo. Para
 * cada pessoa, resolve a melhor foto testando as fontes em ordem de qualidade:
 *   1. API-Football (api-sports.io) — headshots oficiais padronizados (opcional,
 *      exige APIFOOTBALL_KEY);
 *   2. TheSportsDB — recortes/headshots de jogadores (grátis, ótimos p/ álbum);
 *   3. Wikipedia — foto em resolução original (fallback).
 * Salva como `photos/players/<id>.<ext>` / `photos/coaches/<id>.<ext>`.
 * É best-effort: quem não tiver foto é apenas pulado (o app cai no avatar).
 *
 * Projeto acadêmico local — as imagens são usadas apenas para fins didáticos.
 *
 * Uso:
 *   FOOTBALL_API_TOKEN=xxxx node scripts/collect-photos.mjs
 *   (opcional) APIFOOTBALL_KEY=xxxx SPORTSDB_API_KEY=xxxx para melhor cobertura
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.join(__dirname, '..');

const TOKEN = process.env.FOOTBALL_API_TOKEN;
const COMPETITION = process.env.COMPETITION_CODE || 'WC';
// Lista opcional de selecoes a coletar (padrao: todas). Ex.: TEAMS="Brazil,Spain"
const ONLY_TEAMS = (process.env.TEAMS || '')
  .split(',')
  .map((t) => t.trim())
  .filter(Boolean);

if (!TOKEN) {
  console.error('Defina FOOTBALL_API_TOKEN (token do football-data.org).');
  process.exit(1);
}

const playersDir = path.join(ROOT, 'photos', 'players');
const coachesDir = path.join(ROOT, 'photos', 'coaches');
fs.mkdirSync(playersDir, { recursive: true });
fs.mkdirSync(coachesDir, { recursive: true });

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function footballData(pathSuffix) {
  const res = await fetch(`https://api.football-data.org/v4${pathSuffix}`, {
    headers: { 'X-Auth-Token': TOKEN },
  });
  if (!res.ok) throw new Error(`football-data ${res.status} em ${pathSuffix}`);
  return res.json();
}

const USER_AGENT = 'Copa26AlbumDigital/1.0 (academic project)';
const SPORTSDB_KEY = process.env.SPORTSDB_API_KEY || '3'; // '3' = chave de teste pública
const APIFOOTBALL_KEY = process.env.APIFOOTBALL_KEY || '';
const IMG_EXTS = ['jpg', 'jpeg', 'png', 'webp'];

/**
 * `fetch` com novas tentativas e backoff exponencial. As fontes gratuitas
 * (TheSportsDB chave '3', Wikipedia) aplicam limite por minuto e respondem 429
 * quando estouramos a cota; sem retry, jogadores acabam ficando sem foto de
 * forma intermitente. Reencaminha status 429/5xx e erros de rede.
 */
async function fetchWithRetry(url, options = {}, retries = 3, baseDelayMs = 1500) {
  for (let attempt = 0; attempt <= retries; attempt++) {
    try {
      const res = await fetch(url, options);
      if (res.status === 429 || res.status >= 500) {
        if (attempt === retries) return res;
        const wait = baseDelayMs * 2 ** attempt;
        await sleep(wait);
        continue;
      }
      return res;
    } catch (err) {
      if (attempt === retries) throw err;
      await sleep(baseDelayMs * 2 ** attempt);
    }
  }
}

/** Já existe alguma foto (qualquer extensão) para este id? */
function hasPhoto(dir, id) {
  return IMG_EXTS.some((ext) => fs.existsSync(path.join(dir, `${id}.${ext}`)));
}

/** Extensão a partir da URL (svg é ignorado — não serve como figurinha). */
function extFromUrl(url) {
  const clean = url.split('?')[0].toLowerCase();
  if (clean.endsWith('.png')) return 'png';
  if (clean.endsWith('.webp')) return 'webp';
  if (clean.endsWith('.svg')) return null;
  return 'jpg';
}

/**
 * Fonte opcional de MAIOR qualidade: API-Football (api-sports.io). Headshots
 * oficiais e uniformes (cara de figurinha oficial). Só a busca por nome consome
 * cota; as imagens ficam num CDN público. Ignorada se não houver APIFOOTBALL_KEY.
 */
async function apiFootballPhoto(name) {
  if (!APIFOOTBALL_KEY) return null;
  const url = `https://v3.football.api-sports.io/players/profiles?search=${encodeURIComponent(name)}`;
  const res = await fetchWithRetry(url, { headers: { 'x-apisports-key': APIFOOTBALL_KEY } });
  if (!res.ok) return null;
  const data = await res.json();
  return data?.response?.[0]?.player?.photo || null;
}

/**
 * Fonte principal grátis: TheSportsDB. Prioriza `strCutout` (recorte com fundo
 * transparente, ideal para álbum). Chave '3' é o teste público; defina
 * SPORTSDB_API_KEY para mais cota.
 */
async function sportsDbPhoto(name) {
  const url =
    `https://www.thesportsdb.com/api/v1/json/${SPORTSDB_KEY}` +
    `/searchplayers.php?p=${encodeURIComponent(name)}`;
  const res = await fetchWithRetry(url, { headers: { 'User-Agent': USER_AGENT } });
  if (!res.ok) return null;
  const data = await res.json();
  const players = data?.player;
  if (!Array.isArray(players) || players.length === 0) return null;
  const soccer =
    players.find((p) => (p.strSport || '').toLowerCase() === 'soccer') || players[0];
  return soccer.strCutout || soccer.strRender || soccer.strThumb || null;
}

/** Fallback: foto do Wikipedia (originalimage tem a maior resolução). */
async function wikiPhoto(name) {
  const title = encodeURIComponent(name.replace(/ /g, '_'));
  const res = await fetchWithRetry(
    `https://en.wikipedia.org/api/rest_v1/page/summary/${title}`,
    { headers: { 'User-Agent': USER_AGENT } },
  );
  if (!res.ok) return null;
  const data = await res.json();
  return data?.originalimage?.source || data?.thumbnail?.source || null;
}

/**
 * Resolve a melhor URL de foto testando as fontes em ordem de eficacia medida.
 * TheSportsDB vem primeiro: numa amostra de 162 pessoas de 6 selecoes acertou
 * 98%, quase sempre com `strCutout` (recorte 500x500 e fundo transparente, que e
 * o formato de figurinha). A Wikipedia ficou por ultimo porque limita agressivamente
 * (HTTP 429) e inviabiliza uma coleta em lote.
 */
async function resolvePhotoUrl(name) {
  return (
    (await sportsDbPhoto(name)) ||
    (await apiFootballPhoto(name)) ||
    (await wikiPhoto(name)) ||
    null
  );
}

/** Baixa a URL e salva em `<dir>/<id>.<ext>`. Retorna true se salvou. */
async function downloadPhoto(url, dir, id) {
  const ext = extFromUrl(url);
  if (!ext) return false;
  const res = await fetchWithRetry(url, { headers: { 'User-Agent': USER_AGENT } });
  if (!res.ok) return false;
  const buf = Buffer.from(await res.arrayBuffer());
  if (buf.length < 512) return false; // provável placeholder/imagem inválida
  fs.writeFileSync(path.join(dir, `${id}.${ext}`), buf);
  return true;
}

async function main() {
  const data = await footballData(`/competitions/${COMPETITION}/teams`);
  let teams = data.teams || [];
  if (ONLY_TEAMS.length) teams = teams.filter((t) => ONLY_TEAMS.includes(t.name));
  console.log(`${teams.length} equipes selecionadas na competição ${COMPETITION}.`);

  let saved = 0;
  let missing = 0;

  for (const team of teams) {
    if (team.coach?.id && team.coach?.name && !hasPhoto(coachesDir, team.coach.id)) {
      const url = await resolvePhotoUrl(team.coach.name);
      if (url && (await downloadPhoto(url, coachesDir, team.coach.id))) {
        saved++;
        console.log('OK  (técnico)', team.coach.name);
      } else {
        missing++;
        console.log('--  (técnico)', team.coach.name);
      }
      await sleep(400);
    }

    for (const player of team.squad || []) {
      if (hasPhoto(playersDir, player.id)) continue;
      const url = await resolvePhotoUrl(player.name);
      if (url && (await downloadPhoto(url, playersDir, player.id))) {
        saved++;
        console.log('OK ', player.name);
      } else {
        missing++;
        console.log('-- ', player.name);
      }
      await sleep(400);
    }
  }

  console.log(`\nConcluído. Fotos salvas: ${saved} · sem foto: ${missing}`);
  console.log('Próximo passo: python3 scripts/optimize-photos.py (converte para WebP 320px).');
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});

# Copa26 Photo API (privada)

API mínima que serve **apenas as fotos** de jogadores e técnicos, protegida por
uma chave privada (`x-api-key`). A **fonte de dados do app continua sendo o
football-data.org** (requisito do trabalho); esta API resolve só o que aquela
API não fornece: as imagens.

As fotos ficam indexadas pelo **id do jogador/técnico do football-data.org**, o
mesmo id usado pelo app — por isso o casamento foto ↔ pessoa é exato.

## Endpoints

| Método | Rota              | Auth        | Descrição                                  |
|--------|-------------------|-------------|--------------------------------------------|
| GET    | `/health`         | não         | Health check.                              |
| GET    | `/players/:id`    | `x-api-key` | Foto do jogador (`?name=` p/ fallback).    |
| GET    | `/coaches/:id`    | `x-api-key` | Foto do técnico (`?name=` p/ fallback).    |

Se não houver foto local para o id, a API redireciona (302) para um avatar de
iniciais gerado a partir de `?name=` — fallback gracioso.

## Rodar

```bash
cd photo-api
cp .env.example .env      # ajuste PHOTO_API_KEY
npm install
npm start                 # http://localhost:3000
```

Teste:

```bash
curl http://localhost:3000/health
curl -H "x-api-key: copa26-dev-key" "http://localhost:3000/players/44?name=Neymar" -I
```

## Coletar fotos com a camisa da selecao (recomendado)

```bash
FOOTBALL_API_TOKEN=SEU_TOKEN python3 scripts/collect-national-photos.py
FOOTBALL_API_TOKEN=SEU_TOKEN TEAMS="Brazil,Argentina" python3 scripts/collect-national-photos.py
```

Usa **Wikidata + Wikimedia Commons**, onde a maior parte das fotos e de jogos de
selecao — boa parte da propria Copa 2026. Casa o jogador em duas etapas (rotulo
exato e, para os que sobram, data de nascimento), porque so o nome erra: "Wesley"
bate num homonimo e "Vinicius Junior" nao bate em "Vinícius Júnior".

Cobertura medida nas 7 selecoes (189 pessoas): 173 com foto.

> **Nem toda foto serve.** Alguns jogadores so tem foto de clube no Commons e
> outros aparecem de colete de aquecimento por cima da camisa. Confira o
> resultado antes de publicar — nao ha como automatizar esse julgamento.

## Coletar fotos (alternativa: recorte de clube)

Baixa fotos indexadas pelo id do football-data.org, testando as fontes em ordem
de eficácia **medida** (162 pessoas de 6 seleções):

1. **TheSportsDB** — 98% de acerto, quase sempre `strCutout` (recorte 500×500 com
   fundo transparente, formato de figurinha). Grátis, chave de teste `3`;
2. **API-Football** (api-sports.io) — headshots 150×150 padronizados. O CDN de
   imagens é público, mas descobrir o id exige `APIFOOTBALL_KEY` (100 req/dia no
   plano grátis, insuficiente para 1249 jogadores);
3. **Wikipedia** — fotos com uniforme de seleção, porém limita agressivamente
   (HTTP 429) e inviabiliza coleta em lote. Último recurso.

```bash
# todas as 48 seleções (~1249 jogadores, 20-30 min)
FOOTBALL_API_TOKEN=SEU_TOKEN npm run collect

# apenas algumas seleções
FOOTBALL_API_TOKEN=SEU_TOKEN TEAMS="Brazil,Argentina,Spain,Germany,France,England" npm run collect
```

Depois da coleta, reduza as imagens (36 MB → 2,5 MB nas 6 seleções):

```bash
python3 scripts/optimize-photos.py     # requer Pillow
```

As imagens ficam em `photos/players/<id>.webp` e `photos/coaches/<id>.webp`.
Projeto acadêmico local: as imagens são usadas apenas para fins didáticos.

> **Uniforme:** os recortes do TheSportsDB são com camisa de **clube**, não de
> seleção. O enquadramento é do peito para cima, então o uniforme aparece pouco.
> Nenhuma fonte gratuita oferece uniforme de seleção com cobertura comparável.

## Como as fotos chegam ao app

As fotos são **versionadas** neste repositório (`photos/`, ~2,5 MB em WebP). O app,
por padrão, as busca direto do GitHub:

```
https://raw.githubusercontent.com/facom32503-album-copa26/copa26-album-digital/main/photo-api/photos/
```

Isso significa que **um clone limpo já exibe as figurinhas**, sem servidor local,
sem deploy e sem `local.properties`. Só funciona depois que as fotos chegam à
branch `main`.

### Alternativa: Firebase Hosting

O `raw.githubusercontent.com` não é uma CDN de verdade (cache de 5 min e limites
para uso anônimo). Para algo mais robusto, publique no Firebase Hosting:

```bash
npm install -g firebase-tools
firebase login
firebase init hosting        # escolha o projeto; "public directory" = photos
firebase deploy --only hosting
```

O `firebase.json` deste diretório já vem configurado (`public: photos`, cache de
1 ano e CORS liberado). Ao final o CLI imprime a URL do projeto; use-a no
`local.properties` do app:

```
PHOTO_API_BASE_URL=https://SEU-PROJETO.web.app/
```

O app monta `<base>/players/<id>.webp` e `<base>/coaches/<id>.webp`. Quem não
tiver foto (as outras 42 seleções) cai automaticamente nas **iniciais desenhadas
pelo próprio app** — sem rede e sem serviço externo.

## Conexão com o app Android

No `local.properties` do projeto Android, escolha uma das duas bases:

```
# Produção/entrega — funciona em qualquer aparelho, sem servidor local:
PHOTO_API_BASE_URL=https://SEU-PROJETO.web.app/

# Desenvolvimento — servidor Node desta pasta (10.0.2.2 = localhost visto pelo emulador):
PHOTO_API_BASE_URL=http://10.0.2.2:3000/
PHOTO_API_KEY=copa26-dev-key               # deve bater com o .env desta API
```

O app injeta o header `x-api-key` (via Coil) somente nas requisições ao host
configurado — irrelevante no Firebase, necessário no servidor local.

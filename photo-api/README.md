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

## Coletar fotos (executa 1x)

Baixa fotos indexadas pelo id do football-data.org, testando as fontes em ordem
de qualidade (headshots estilo figurinha primeiro):

1. **API-Football** (api-sports.io) — headshots oficiais e uniformes (opcional,
   exige `APIFOOTBALL_KEY`);
2. **TheSportsDB** — recortes/headshots de jogadores, grátis (chave de teste `3`);
3. **Wikipedia** — foto em resolução original (fallback).

```bash
FOOTBALL_API_TOKEN=SEU_TOKEN npm run collect
# melhor cobertura/qualidade:
FOOTBALL_API_TOKEN=SEU_TOKEN APIFOOTBALL_KEY=SUA_CHAVE npm run collect
```

As imagens são salvas em `photos/players/<id>.<ext>` e `photos/coaches/<id>.<ext>`
(png/jpg/webp). Projeto acadêmico local: as imagens são usadas apenas para fins
didáticos.

## Conexão com o app Android

No `local.properties` do projeto Android:

```
PHOTO_API_BASE_URL=http://10.0.2.2:3000/   # 10.0.2.2 = localhost visto pelo emulador
PHOTO_API_KEY=copa26-dev-key               # deve bater com o .env desta API
```

O app injeta o header `x-api-key` (via Coil) somente nas requisições a este host.

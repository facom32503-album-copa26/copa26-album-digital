# Copa26 Álbum Digital

Aplicativo Android nativo (Kotlin + Jetpack Compose) de **álbum de figurinhas digital**
da Copa 2026, desenvolvido para a disciplina **FACOM32503 — Processo de Desenvolvimento
Mobile (PDM)**.

## Stack e Requisitos

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Arquitetura:** MVVM (Model–View–ViewModel)
- **Build:** Gradle (Kotlin DSL) + version catalog (`gradle/libs.versions.toml`)
- **SDK:** `minSdk = 24`, `targetSdk`/`compileSdk = 36`, `JavaVersion.VERSION_11`

## Configuração de `local.properties`

O app consome dados reais da API [football-data.org](https://www.football-data.org) e,
opcionalmente, de uma API privada de fotos (`photo-api/`). Ambas exigem chaves que **não**
são versionadas — cada dev cria seu próprio `local.properties` na raiz do projeto:

```bash
cp local.properties.example local.properties
# edite local.properties e preencha FOOTBALL_API_TOKEN com seu token
```

- `FOOTBALL_API_TOKEN`: obrigatório. Sem ele (ou com um token inválido), toda chamada à
  football-data.org falha e a tela de equipes exibe erro de "competição não encontrada" —
  crie uma conta gratuita em https://www.football-data.org/client/register para gerar o seu.
  Validado em 2026-07-28: com um token válido, `GET /v4/competitions/WC/teams` e
  `GET /v4/teams/{id}` retornam `squad` e `coach` inline mesmo no plano gratuito — o app
  carrega os 48 times, elenco (26 jogadores) e técnico de cada seleção normalmente.
  O plano gratuito limita a **10 requisições/minuto** (header `X-Requests-Available-Minute`);
  navegação muito rápida entre telas pode esbarrar nisso.
- `PHOTO_API_BASE_URL` / `PHOTO_API_KEY`: só necessários se for rodar a `photo-api/` local
  (ver `photo-api/README.md`); já vêm com padrão para o emulador Android.

## Como compilar e executar

Pré-requisitos: Android Studio (recomendado) ou JDK 11 + Android SDK configurados.

```bash
# Clonar
git clone https://github.com/facom32503-album-copa26/copa26-album-digital.git
cd copa26-album-digital

# Build de debug
./gradlew assembleDebug

# Testes unitários
./gradlew testDebugUnitTest

# Testes instrumentados (emulador/dispositivo conectado)
./gradlew connectedAndroidTest

# Bundle de release para publicação (AAB)
./gradlew bundleRelease
```

Para rodar no emulador/dispositivo, abra o projeto no Android Studio e use **Run ▶**,
ou instale o APK de debug gerado em `app/build/outputs/apk/debug/`.

## Estrutura do Projeto

```
app/src/main/java/com/example/copa26_album_digital/
├── MainActivity.kt          # Ponto de entrada (ComponentActivity + setContent)
└── ui/theme/                # Tema Material 3 (Theme.kt, Color.kt, Type.kt)
app/src/main/res/            # Recursos (drawable, values/strings.xml, etc.)
gradle/libs.versions.toml    # Version catalog (todas as dependências)
.github/instructions/        # Diretrizes de desenvolvimento (MVVM, Compose, etc.)
```

## Convenções de Código

As regras de desenvolvimento estão documentadas em `.github/instructions/` e devem ser
seguidas obrigatoriamente. Resumo:

- MVVM: lógica de negócio no `ViewModel` (via `StateFlow`); View "burra".
- Composables em PascalCase e substantivos, puros, com `modifier: Modifier = Modifier`.
- SP para fontes, DP para dimensões; listas em `LazyColumn`.
- Textos em `res/values/strings.xml`; imagens decorativas com `contentDescription = null`.
- Dependências apenas via `libs.versions.toml` (nunca versões hardcoded).

## Fluxo de Trabalho da Equipe (Git)

- **Repositório compartilhado** na organização `facom32503-album-copa26`.
- **Branches por membro/feature**: ninguém commita direto na `main`.
- **Pull Requests obrigatórios**, revisados pelo **Líder do Projeto** antes do merge.
- **Gestão de tarefas** via **GitHub Projects**.
- Manter este **README** sempre atualizado com instruções de compilação e uso.

### Padrão de branches

```
main                  # protegida, só recebe merge via PR aprovado
feature/<descricao>   # novas funcionalidades
fix/<descricao>       # correções
```

## Equipe

Projeto em grupo (até 8 integrantes) com papéis definidos: Líder do Projeto,
Desenvolvedores Android, Engenheiros de Dados e Designers/UX.

Disciplina ministrada pelo **Prof. Cláudio C. Rodrigues** — UFU/BSI.


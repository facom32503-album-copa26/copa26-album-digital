# Copilot Instructions — Copa26 Álbum Digital

Regras operacionais para agentes de IA. Derivadas de `INSTRUCTIONS.md` e da estrutura
real do projeto. **Nenhuma delas pode ser violada.**

As regras estão divididas por tema em `.github/instructions/` (cada arquivo tem um
cabeçalho `applyTo` que define a quais arquivos ele se aplica):

| Arquivo | Tema |
| --- | --- |
| `01-mvvm.instructions.md` | Arquitetura MVVM: View "burra", estado no `ViewModel`, `StateFlow`/`LiveData` |
| `02-composable-nomenclatura.instructions.md` | PascalCase + substantivos, pureza, `modifier` obrigatório |
| `03-composable-estado.instructions.md` | `remember` vs `rememberSaveable` vs estado no `ViewModel` |
| `04-layout-unidades.instructions.md` | SP p/ fontes, DP p/ dimensões, `Column`/`Row`/`Box`, `LazyColumn` |
| `05-preview.instructions.md` | `@Preview` com defaults dentro de `Copa26albumdigitalTheme` |
| `06-recursos-acessibilidade.instructions.md` | `drawable/`, `contentDescription = null`, `strings.xml` |
| `07-navegacao.instructions.md` | Navigation Compose: `NavController` + `NavHost`, Safe Args |
| `08-manifesto.instructions.md` | Declaração de componentes/permissões no `AndroidManifest.xml` |
| `09-ciclo-vida-dados.instructions.md` | `onCreate`, Intents, `Parcelable` (nunca `Serializable`) |
| `10-dependencias-gradle.instructions.md` | Version catalog (`libs.*`), nunca hardcode de versão |
| `11-build-checklist.instructions.md` | Comandos de build/test + checklist final de conformidade |

## Contexto mínimo do projeto

- App Android nativo (Jetpack Compose), template "Empty Activity".
- Pacote base: `com.example.copa26_album_digital`; entrada única em `MainActivity.kt`.
- Tema em `ui/theme/` — sempre envolver a UI em `Copa26albumdigitalTheme { ... }`.
- `minSdk = 24`, `targetSdk`/`compileSdk = 36`, `JavaVersion.VERSION_11`.
- Antes de finalizar: `./gradlew assembleDebug` deve compilar sem erros.


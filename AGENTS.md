# AGENTS.md — Copa26 Álbum Digital

Guia operacional para agentes de IA. As regras abaixo derivam de `INSTRUCTIONS.md` e da
estrutura real do projeto. **Nenhuma delas pode ser violada.**

## Visão Geral do Projeto

- App Android nativo (Jetpack Compose) — template "Empty Activity".
- Pacote base: `com.example.copa26_album_digital` (`app/src/main/java/.../`).
- Ponto de entrada único: `MainActivity.kt` (`ComponentActivity` + `setContent`).
- Tema centralizado em `ui/theme/` (`Theme.kt`, `Color.kt`, `Type.kt`) — use sempre
  `Copa26albumdigitalTheme { ... }` como wrapper raiz da UI e nos `@Preview`.
- Build via Gradle KTS + version catalog. Dependências SÓ em `gradle/libs.versions.toml`
  referenciadas como `libs.*` em `app/build.gradle.kts`. **Nunca** hardcode versões.
- `minSdk = 24`, `targetSdk`/`compileSdk = 36`, `JavaVersion.VERSION_11`.

## Comandos de Build/Test (macOS/zsh)

```bash
./gradlew assembleDebug          # build debug
./gradlew testDebugUnitTest      # testes unitários (test/)
./gradlew connectedAndroidTest   # testes instrumentados (androidTest/)
./gradlew bundleRelease          # AAB para publicação (preferir sobre APK)
```

## Regras de Arquitetura (MVVM — obrigatório)

- **NUNCA** misture lógica de negócio com UI. Separe em Model / View / ViewModel.
- View (`Activity`/`Composable`) é "burra": apenas exibe estado e emite eventos.
- Estado que sobrevive a rotação/recriação fica em `ViewModel` (nunca na `Activity`).
- Exponha estado via `StateFlow`/`LiveData`; a UI observa e recompõe automaticamente.
  Não coloque click handlers de negócio dentro da `Activity`.
- Inicialização lógica e `setContent` ficam em `onCreate()`.

## Regras de Jetpack Compose (obrigatório)

- Funções `@Composable` usam **PascalCase** e são **substantivos** (`GreetingCard`,
  `AlbumScreen`). **PROIBIDO** verbos (`DrawCard`), preposições (`CardWithText`),
  adjetivos ou advérbios isolados.
- `@Composable` retorna `Unit` (sem valor de retorno) e é **pura** — sem efeitos
  colaterais durante recomposição.
- Todo `@Composable` deve aceitar um parâmetro `modifier: Modifier = Modifier`
  (ver `Greeting` em `MainActivity.kt`).
- Estado: `remember` para composição; `rememberSaveable` para sobreviver à recriação.
- Unidades: **SP** apenas para fontes; **DP** para padding/espaçamento/dimensões.
- Layouts: `Column` (vertical), `Row` (horizontal), `Box` (sobreposição);
  listas SEMPRE com `LazyColumn`.
- `@Preview` deve fornecer valores padrão para todos os parâmetros e envolver a UI
  no `Copa26albumdigitalTheme` (ver `GreetingPreview`).

## Recursos e Acessibilidade

- Imagens em `app/src/main/res/drawable/`.
- Imagens decorativas: densidade "No Density" e `contentDescription = null`
  (ignoradas pelo TalkBack).
- Strings em `res/values/strings.xml`; cores de tema em `ui/theme/Color.kt`.

## Navegação

- Múltiplas telas → Navigation Compose com `NavController` (lógica) + `NavHost`
  (contêiner). Não gerencie fragmentos manualmente. Use Safe Args para parâmetros.

## Manifesto e Componentes

- Todo componente/permissão/metadado deve ser declarado em `AndroidManifest.xml`.
- Novas `Activity` precisam de entrada no manifesto.

## Comunicação e Dados

- Intents explícitas para componentes internos; implícitas para ações do sistema.
- Para passar objetos entre componentes use **Parcelable** — nunca `Serializable`.

## Checklist antes de finalizar qualquer mudança

- [ ] Zero "red text": imports otimizados, sem referências não usadas.
- [ ] Composables em PascalCase e substantivos (sem verbos/adjetivos).
- [ ] Imagens decorativas com `contentDescription = null`.
- [ ] SP para textos, DP para paddings/dimensões.
- [ ] `@Preview` com valores padrão e dentro do tema do app.
- [ ] Lógica de clique/estado fora da `Activity`, no `ViewModel`.
- [ ] Dependências novas adicionadas ao `libs.versions.toml` (via `libs.*`).
- [ ] `./gradlew assembleDebug` compila sem erros.


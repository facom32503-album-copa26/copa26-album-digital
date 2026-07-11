---
applyTo: "**"
---
# Build, Testes e Entrega + Checklist Final

## Comandos (macOS/zsh)

```bash
./gradlew assembleDebug          # build debug
./gradlew testDebugUnitTest      # testes unitários (app/src/test/)
./gradlew connectedAndroidTest   # testes instrumentados (app/src/androidTest/)
./gradlew bundleRelease          # AAB para publicação (preferir sobre APK)
```

- Publicação: gerar **AAB** (`bundleRelease`). APK só para teste manual/instalação direta.
- Segurança: aplicar regras de ProGuard/ofuscação para builds de release.

## Checklist antes de finalizar QUALQUER mudança

- [ ] Zero "red text": imports otimizados, sem referências não usadas.
- [ ] Composables em PascalCase e substantivos (sem verbos/preposições/adjetivos).
- [ ] Todo `@Composable` tem `modifier: Modifier = Modifier` e retorna `Unit`.
- [ ] Imagens decorativas com `contentDescription = null`; textos em `strings.xml`.
- [ ] SP para fontes, DP para paddings/dimensões; listas em `LazyColumn`.
- [ ] `@Preview` com valores padrão e dentro de `Copa26albumdigitalTheme`.
- [ ] Estado/lógica de clique fora da `Activity`, no `ViewModel` (`StateFlow`/`LiveData`).
- [ ] Objetos transportados via `Parcelable` (nunca `Serializable`).
- [ ] Novos componentes/permissões declarados em `AndroidManifest.xml`.
- [ ] Dependências novas no `libs.versions.toml` e usadas via `libs.*`.
- [ ] `./gradlew assembleDebug` compila sem erros.


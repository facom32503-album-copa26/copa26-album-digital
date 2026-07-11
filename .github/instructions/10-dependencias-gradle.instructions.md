---
applyTo: "**/*.{kts,toml}"
---
# Dependências e Gradle (Version Catalog obrigatório)

## Regras

- **Nunca** hardcode versões em `build.gradle.kts`. Toda dependência/plugin é
  declarada em `gradle/libs.versions.toml` e referenciada como `libs.*`.
- Alias com `-` no TOML vira `.` no acessor (`androidx-core-ktx` → `libs.androidx.core.ktx`).
- Configuração atual (não reduzir sem motivo): `minSdk = 24`, `targetSdk`/`compileSdk = 36`,
  `JavaVersion.VERSION_11`.

## Faça — adicionar uma dependência

`gradle/libs.versions.toml`:

```toml
[versions]
navigationCompose = "2.8.0"

[libraries]
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
```

`app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.androidx.navigation.compose)
}
```

## Não faça

```kotlin
// ERRADO: versão hardcoded fora do catálogo
implementation("androidx.navigation:navigation-compose:2.8.0")
```


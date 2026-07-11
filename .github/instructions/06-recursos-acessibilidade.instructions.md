---
applyTo: "**/*.{kt,xml}"
---
# Recursos e Acessibilidade

## Regras

- Imagens ficam em `app/src/main/res/drawable/`.
- Imagem **decorativa**: importar com densidade "No Density" e usar
  `contentDescription = null` (o TalkBack a ignora).
- Imagem **informativa**: `contentDescription` com texto vindo de `strings.xml`.
- Textos visíveis SEMPRE em `res/values/strings.xml` (nunca string literal na UI).
- Cores do tema em `ui/theme/Color.kt` (usar via `MaterialTheme.colorScheme`).

## Faça

```kotlin
// Decorativa
Image(
    painter = painterResource(R.drawable.fundo_estadio),
    contentDescription = null,
)

// Informativa
Image(
    painter = painterResource(R.drawable.escudo_selecao),
    contentDescription = stringResource(R.string.escudo_selecao_desc),
)

Text(text = stringResource(R.string.titulo_album))
```

## Não faça

```kotlin
Text(text = "Álbum da Copa")                    // ERRADO: literal, use strings.xml
Image(painter = ..., contentDescription = "")   // ERRADO: use null se decorativa
```


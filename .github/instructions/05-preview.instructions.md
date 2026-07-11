---
applyTo: "**/*.kt"
---
# Previews (obrigatório)

## Regras

- Toda `@Preview` fornece **valores padrão para todos os parâmetros** do Composable
  (para renderizar no Android Studio sem erro).
- A `@Preview` envolve a UI no tema do app: `Copa26albumdigitalTheme { ... }`.
- Nome da função de preview em PascalCase e substantivo (ex: `GreetingPreview`).

## Faça

```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Copa26albumdigitalTheme {
        Greeting(name = "Android")   // valor padrão fornecido
    }
}
```

## Não faça

```kotlin
@Preview
@Composable
fun GreetingPreview() {
    Greeting(name = /* faltando */) // ERRADO: parâmetro sem valor
    // ERRADO: sem Copa26albumdigitalTheme envolvendo
}
```

Referência viva: `GreetingPreview` em `MainActivity.kt`.


---
applyTo: "**/*.kt"
---
# Nomenclatura e Pureza de Composables (obrigatório)

## Regras

- Funções `@Composable` usam **PascalCase** e são **substantivos**.
- **PROIBIDO**: verbos, preposições nominais, adjetivos ou advérbios isolados.
- `@Composable` retorna `Unit` (sem `return` de valor).
- `@Composable` é **pura**: sem efeitos colaterais durante a recomposição
  (nada de I/O, escrita em disco, chamadas de rede diretas no corpo).
- Todo `@Composable` aceita `modifier: Modifier = Modifier` como parâmetro
  (com esse nome e valor padrão), repassado ao layout raiz.

## Faça

```kotlin
@Composable
fun GreetingCard(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun AlbumScreen(modifier: Modifier = Modifier) { /* ... */ }
```

## Não faça

```kotlin
@Composable fun DrawCard() { }          // ERRADO: verbo
@Composable fun CardWithText() { }       // ERRADO: preposição nominal
@Composable fun Bonito() { }             // ERRADO: adjetivo
@Composable fun renderList(): List<Int>  // ERRADO: retorna valor + verbo/camelCase
```

Nomes bons: `GreetingCard`, `AlbumScreen`, `StickerGrid`, `BirthdayCardPreview`.
Nomes ruins: `ShowAlbum`, `LoadStickers`, `CardWithImage`, `DrawGrid`.


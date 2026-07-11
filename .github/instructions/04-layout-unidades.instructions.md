---
applyTo: "**/*.kt"
---
# Layouts e Unidades de Medida (obrigatório)

## Unidades

- **SP** (`.sp`): EXCLUSIVAMENTE para tamanhos de fonte (respeita acessibilidade).
- **DP** (`.dp`): para padding, espaçamento, largura, altura e demais dimensões.

## Layouts

- `Column` → empilhamento vertical.
- `Row` → arranjo horizontal.
- `Box` → sobreposição de elementos.
- Listas → SEMPRE `LazyColumn` (nunca `Column` + `forEach` para listas grandes).

## Faça

```kotlin
Text(text = titulo, fontSize = 20.sp)           // fonte em SP

Column(modifier = Modifier.padding(16.dp)) {    // dimensão em DP
    Spacer(Modifier.height(8.dp))
}

LazyColumn {
    items(figurinhas) { figurinha -> StickerCard(figurinha) }
}
```

## Não faça

```kotlin
Text(text = titulo, fontSize = 20.dp)           // ERRADO: fonte em DP
Column(modifier = Modifier.padding(16.sp))      // ERRADO: dimensão em SP

Column { figurinhas.forEach { StickerCard(it) } } // ERRADO: use LazyColumn
```


---
applyTo: "**/*.kt"
---
# Gestão de Estado no Compose (obrigatório)

## Regras

- `remember`: mantém valor durante a composição (perde no recriar da Activity).
- `rememberSaveable`: sobrevive à recriação (rotação, morte de processo).
- Use `by` com delegação para ler/escrever estado sem `.value` repetido.
- Estado de negócio NÃO fica no Composable — vai para o `ViewModel` (ver 01-mvvm).
  Use `remember`/`rememberSaveable` apenas para estado local de UI efêmero.

## Faça

```kotlin
// Estado de UI puramente local (ex: se um card está expandido)
var expandido by rememberSaveable { mutableStateOf(false) }

// Valor derivado/caro memorizado durante a composição
val formatado = remember(nome) { nome.uppercase() }
```

## Não faça

```kotlin
// ERRADO: perde valor ao rotacionar quando o estado precisava sobreviver
var contador by remember { mutableStateOf(0) }

// ERRADO: estado de negócio no Composable em vez do ViewModel
var listaDeFigurinhas by remember { mutableStateOf(carregarDoBanco()) }
```

Regra prática: sobrevive à rotação? → `rememberSaveable`. É de negócio? → `ViewModel`.


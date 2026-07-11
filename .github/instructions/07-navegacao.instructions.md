---
applyTo: "**/*.kt"
---
# Navegação (Navigation Compose)

## Regras

- Múltiplas telas → Navigation Compose. `NavController` = lógica de navegação;
  `NavHost` = contêiner das rotas/destinos.
- **Proibido** gerenciar fragmentos manualmente.
- Use rotas/destinos claros; para parâmetros tipados, use Safe Args (evita erro
  de tipo na passagem de argumentos).
- Deixe o `NavController` tratar os botões "Up" e "Back" — não implemente pilha manual.

## Faça

```kotlin
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "album", modifier = modifier) {
        composable("album") {
            AlbumScreen(onFigurinhaSelecionada = { id ->
                navController.navigate("figurinha/$id")
            })
        }
        composable(
            route = "figurinha/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            StickerScreen(id = id)
        }
    }
}
```

## Não faça

- Trocar telas com `supportFragmentManager.beginTransaction()`.
- Guardar a pilha de navegação em variáveis próprias.


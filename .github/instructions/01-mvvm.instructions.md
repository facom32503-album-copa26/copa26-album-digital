---
applyTo: "**/*.kt"
---
# Arquitetura MVVM (obrigatório)

Separe SEMPRE em Model / View / ViewModel. Lógica de negócio e lógica de UI **nunca**
no mesmo componente. Estado que sobrevive a rotação/recriação vive no `ViewModel`.

## Regras

- View (`Activity`/`Composable`) é "burra": só exibe estado e emite eventos.
- Estado exposto via `StateFlow` (preferir) ou `LiveData`; a UI observa e recompõe.
- **Proibido** click handler de negócio dentro da `Activity`/`Composable`.
- Nada de `var` mutável de estado guardado na `Activity`.

## Faça

```kotlin
// AlbumViewModel.kt
class AlbumViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    fun onStickerColado(id: Int) {
        _uiState.update { it.copy(colados = it.colados + id) }
    }
}

// AlbumScreen.kt — View observa e delega eventos
@Composable
fun AlbumScreen(
    uiState: AlbumUiState,
    onStickerColado: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // apenas renderiza uiState e chama onStickerColado(...)
}
```

## Não faça

```kotlin
// ERRADO: lógica e estado dentro da Activity
class MainActivity : ComponentActivity() {
    private var colados = mutableListOf<Int>() // estado morre na rotação
    // ... onClick que altera 'colados' diretamente
}
```

- `MainActivity` deve conter apenas inicialização e `setContent` em `onCreate()`.


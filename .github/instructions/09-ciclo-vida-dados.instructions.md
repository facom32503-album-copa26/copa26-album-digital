---
applyTo: "**/*.kt"
---
# Ciclo de Vida, Intents e Passagem de Dados

## Ciclo de vida

- `Activity.onCreate()` é o ponto de entrada: inicialização lógica + `setContent`.
- Componentes são efêmeros: o SO pode destruí-los a qualquer momento. Estado que
  precisa sobreviver vai para `ViewModel`/`rememberSaveable`, não para campos da Activity.

## Intents

- **Explícitas**: para componentes internos do app (classe alvo conhecida).
- **Implícitas**: para ações do sistema (via `Intent` com action + `Intent Filters`).

```kotlin
// Explícita (interna)
startActivity(Intent(this, DetalheActivity::class.java))

// Implícita (sistema)
startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://...")))
```

## Parcelable, nunca Serializable

- Para transportar objetos entre componentes use **Parcelable** (otimizado p/ IPC).
- **Proibido** `Serializable` para esse fim.

```kotlin
@Parcelize
data class Figurinha(val id: Int, val nome: String) : Parcelable
```

Requer o plugin `kotlin-parcelize` (adicionar via `libs.versions.toml`; ver 10-dependencias).


package com.example.copa26_album_digital.domain.util

/**
 * Envólucro de resultado para operações que podem falhar (rede, cache, etc.).
 * Permite ao ViewModel reagir a sucesso ou erro sem lançar exceções na UI.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>
}

package com.example.copa26_album_digital.domain.model

/**
 * Modelo de domínio que representa o treinador de uma equipe.
 *
 * Exibido na PlayerDetailScreen (variação treinador) com foto, nome e perfil.
 */
data class Coach(
    val id: Int,
    val name: String,
    val nationality: String,
    val photoUrl: String,
    val profile: String,
)

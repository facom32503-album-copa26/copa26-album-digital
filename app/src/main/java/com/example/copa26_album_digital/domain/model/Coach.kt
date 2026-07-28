package com.example.copa26_album_digital.domain.model

/** Treinador de uma equipe, exibido na PersonDetailScreen. */
data class Coach(
    val id: Int,
    val name: String,
    val nationality: String,
    val photoUrl: String,
)

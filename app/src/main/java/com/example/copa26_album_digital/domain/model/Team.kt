package com.example.copa26_album_digital.domain.model

/** Equipe participante de uma competição, com elenco e treinador. */
data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val crestUrl: String,
    val colors: List<String>,
    /** Nome do estádio, cru — a frase exibida é montada na UI. */
    val venue: String,
    val victories: Int,
    val players: List<Player>,
    val coach: Coach?,
)

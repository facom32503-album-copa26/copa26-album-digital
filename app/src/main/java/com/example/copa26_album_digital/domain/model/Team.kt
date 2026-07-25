package com.example.copa26_album_digital.domain.model

/**
 * Modelo de domínio que representa uma equipe participante de uma competição.
 *
 * Carregada na TeamScreen: contém escudo, cores oficiais, descrição, número de
 * vitórias (selos de troféus), o elenco de jogadores e o treinador.
 */
data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val crestUrl: String,
    val colors: List<String>,
    val description: String,
    val victories: Int,
    val players: List<Player>,
    val coach: Coach?,
)

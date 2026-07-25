package com.example.copa26_album_digital.domain.model

/**
 * Modelo de domínio que representa um jogador (figurinha individual).
 *
 * Exibido na PlayerDetailScreen com foto, nome, posição, número da camisa e
 * estatísticas de desempenho.
 */
data class Player(
    val id: Int,
    val name: String,
    val position: String,
    val shirtNumber: Int,
    val nationality: String,
    val photoUrl: String,
    val stats: PlayerStats,
)

/**
 * Estatísticas de desempenho de um jogador exibidas na tela de detalhes.
 */
data class PlayerStats(
    val games: Int,
    val goals: Int,
    val assists: Int,
)

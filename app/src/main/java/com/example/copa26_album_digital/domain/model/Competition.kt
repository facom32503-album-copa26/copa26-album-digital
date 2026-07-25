package com.example.copa26_album_digital.domain.model

/**
 * Modelo de domínio que representa uma competição esportiva.
 *
 * Agregado raiz do álbum: reúne os metadados do evento (nome, edição e troféu)
 * e a lista de equipes participantes. É a entidade carregada na CompetitionScreen.
 */
data class Competition(
    val id: Int,
    val name: String,
    val code: String,
    val edition: String,
    val emblemUrl: String,
    val teams: List<Team>,
)

package com.example.copa26_album_digital.navigation

/**
 * Rotas e chaves de argumentos da navegação (Navigation Compose).
 * Centraliza os destinos para evitar strings mágicas espalhadas na UI.
 */
object AppDestinations {

    const val ARG_TEAM_ID = "teamId"
    const val ARG_PERSON_ID = "personId"

    const val COMPETITION_ROUTE = "competition"
    const val TEAMS_ROUTE = "teams"
    const val TEAM_ROUTE = "team/{$ARG_TEAM_ID}"
    const val PERSON_ROUTE = "person/{$ARG_TEAM_ID}/{$ARG_PERSON_ID}"

    fun team(teamId: Int): String = "team/$teamId"

    fun person(teamId: Int, personId: Int): String = "person/$teamId/$personId"
}

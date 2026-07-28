package com.example.copa26_album_digital.ui.preview

import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.model.PlayerStats
import com.example.copa26_album_digital.domain.model.Team

/** Dados de exemplo das funções `@Preview` — nenhum preview precisa de rede, banco ou ViewModel. */
internal object PreviewData {

    val player = Player(
        id = 3218,
        name = "Lionel Messi",
        position = "Offence",
        shirtNumber = 10,
        nationality = "Argentina",
        photoUrl = "",
        stats = PlayerStats(games = 7, goals = 5, assists = 3),
    )

    val coach = Coach(
        id = 11884,
        name = "Lionel Scaloni",
        nationality = "Argentina",
        photoUrl = "",
    )

    val team = Team(
        id = 762,
        name = "Argentina",
        shortName = "Argentina",
        crestUrl = "",
        colors = listOf("Sky Blue", "White"),
        venue = "Estadio Monumental",
        victories = 3,
        players = listOf(
            player,
            player.copy(id = 6, name = "Emiliano Martínez", position = "Goalkeeper", shirtNumber = 23),
            player.copy(id = 7, name = "Julián Álvarez", position = "Offence", shirtNumber = 9),
            player.copy(id = 8, name = "Enzo Fernández", position = "Midfield", shirtNumber = 24),
        ),
        coach = coach,
    )

    val teams = listOf(
        team,
        team.copy(id = 764, name = "Brazil", shortName = "Brazil", victories = 5),
        team.copy(id = 773, name = "France", shortName = "France", victories = 2),
        team.copy(id = 799, name = "Croatia", shortName = "Croatia", victories = 0),
    )
}

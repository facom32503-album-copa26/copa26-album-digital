package com.example.copa26_album_digital.data.local

import com.example.copa26_album_digital.data.local.entity.CoachEntity
import com.example.copa26_album_digital.data.local.entity.PlayerEntity

/**
 * Fonte de dados local (seed) com elencos curados.
 *
 * O plano gratuito da football-data.org não expõe `squad`/`coach` no endpoint de
 * time, então usamos este seed como fallback para garantir o requisito de "5
 * jogadores + treinador por equipe". As chaves são os `id` reais dos times na API.
 */
class SquadSeed {

    private fun avatar(name: String): String =
        "https://ui-avatars.com/api/?background=random&name=" + name.replace(" ", "+")

    /** Retorna o elenco de fallback para um time, ou vazio se não houver seed. */
    fun playersFor(teamId: Int): List<PlayerEntity> {
        val raw = squads[teamId] ?: return emptyList()
        return raw.map { (name, position, number, games, goals, assists) ->
            PlayerEntity(
                id = teamId * 100 + number,
                teamId = teamId,
                name = name,
                position = position,
                shirtNumber = number,
                nationality = "—",
                photoUrl = avatar(name),
                games = games,
                goals = goals,
                assists = assists,
            )
        }
    }

    /** Retorna o treinador de fallback para um time, ou null se não houver seed. */
    fun coachFor(teamId: Int): CoachEntity? {
        val name = coaches[teamId] ?: return null
        return CoachEntity(
            teamId = teamId,
            id = teamId * 100 + 99,
            name = name,
            nationality = "—",
            photoUrl = avatar(name),
            profile = "Treinador da equipe.",
        )
    }

    private data class SeedPlayer(
        val name: String,
        val position: String,
        val number: Int,
        val games: Int,
        val goals: Int,
        val assists: Int,
    )

    private val squads: Map<Int, List<SeedPlayer>> = mapOf(
        // Arsenal FC
        57 to listOf(
            SeedPlayer("David Raya", "Goleiro", 22, 30, 0, 0),
            SeedPlayer("William Saliba", "Zagueiro", 2, 28, 2, 1),
            SeedPlayer("Declan Rice", "Volante", 41, 32, 7, 8),
            SeedPlayer("Martin Ødegaard", "Meia", 8, 30, 12, 10),
            SeedPlayer("Bukayo Saka", "Atacante", 7, 31, 16, 13),
        ),
        // Manchester City FC
        65 to listOf(
            SeedPlayer("Ederson", "Goleiro", 31, 29, 0, 1),
            SeedPlayer("Rúben Dias", "Zagueiro", 3, 27, 1, 2),
            SeedPlayer("Rodri", "Volante", 16, 30, 8, 9),
            SeedPlayer("Kevin De Bruyne", "Meia", 17, 26, 10, 18),
            SeedPlayer("Erling Haaland", "Atacante", 9, 31, 27, 6),
        ),
        // Real Madrid (id 86)
        86 to listOf(
            SeedPlayer("Thibaut Courtois", "Goleiro", 1, 28, 0, 0),
            SeedPlayer("Antonio Rüdiger", "Zagueiro", 22, 30, 3, 1),
            SeedPlayer("Jude Bellingham", "Meia", 5, 32, 19, 9),
            SeedPlayer("Vinícius Júnior", "Atacante", 7, 31, 22, 11),
            SeedPlayer("Kylian Mbappé", "Atacante", 9, 30, 28, 7),
        ),
    )

    private val coaches: Map<Int, String> = mapOf(
        57 to "Mikel Arteta",
        65 to "Pep Guardiola",
        86 to "Carlo Ancelotti",
    )
}

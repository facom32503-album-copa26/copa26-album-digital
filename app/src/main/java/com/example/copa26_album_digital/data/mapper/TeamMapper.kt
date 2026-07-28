package com.example.copa26_album_digital.data.mapper

import com.example.copa26_album_digital.BuildConfig
import com.example.copa26_album_digital.data.local.entity.CoachEntity
import com.example.copa26_album_digital.data.local.entity.PlayerEntity
import com.example.copa26_album_digital.data.local.entity.TeamEntity
import com.example.copa26_album_digital.data.remote.dto.CoachDto
import com.example.copa26_album_digital.data.remote.dto.PersonDto
import com.example.copa26_album_digital.data.remote.dto.TeamDto
import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.model.PlayerStats
import com.example.copa26_album_digital.domain.model.Team

private const val PLACEHOLDER_CREST = "https://via.placeholder.com/150?text=Escudo"

/**
 * Títulos reais de Copa do Mundo por seleção, indexados pelo id da equipe na
 * football-data.org. Como a API não expõe conquistas, mantemos este mapa fixo
 * (dado histórico e estável). Seleções fora do mapa contam 0.
 */
private val WORLD_CUP_TITLES: Map<Int, Int> = mapOf(
    764 to 5, // Brasil
    759 to 4, // Alemanha
    762 to 3, // Argentina
    773 to 2, // França
    758 to 2, // Uruguai
    770 to 1, // Inglaterra
    760 to 2, // Espanha (2010 e 2026)
)

private fun personPhotoUrl(kind: String, id: Int): String =
    "${BuildConfig.PHOTO_API_BASE_URL.trimEnd('/')}/$kind/$id.webp"

private fun parseColors(clubColors: String?): List<String> {
    if (clubColors.isNullOrBlank()) return listOf("#1E1E1E", "#FFFFFF")
    return clubColors.split("/", ",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

/**
 * Converte o DTO de equipe em entidade de cache, associando-a à sua competição.
 * O número de títulos vem do mapa fixo [WORLD_CUP_TITLES] (dado histórico), já
 * que a API não expõe conquistas.
 */
fun TeamDto.toEntity(competitionId: Int): TeamEntity = TeamEntity(
    id = id,
    competitionId = competitionId,
    name = name,
    shortName = shortName ?: name,
    crestUrl = crest ?: PLACEHOLDER_CREST,
    colors = parseColors(clubColors).joinToString("|"),
    venue = venue.orEmpty(),
    victories = WORLD_CUP_TITLES[id] ?: 0,
)

/** Converte um jogador do `squad` em entidade de cache. As estatísticas de
 * desempenho (jogos/gols/assistências) vêm do endpoint de artilheiros da
 * competição; quem não aparece na lista fica zerado. */
fun PersonDto.toPlayerEntity(
    teamId: Int,
    games: Int = 0,
    goals: Int = 0,
    assists: Int = 0,
): PlayerEntity = PlayerEntity(
    id = id,
    teamId = teamId,
    name = name,
    position = position ?: "Indefinido",
    shirtNumber = shirtNumber ?: 0,
    nationality = nationality ?: "—",
    photoUrl = personPhotoUrl("players", id),
    games = games,
    goals = goals,
    assists = assists,
)

/** Converte o treinador do DTO de equipe em entidade de cache. */
fun CoachDto.toEntity(teamId: Int): CoachEntity? {
    val coachId = id ?: return null
    val coachName = name ?: return null
    return CoachEntity(
        teamId = teamId,
        id = coachId,
        name = coachName,
        nationality = nationality ?: "—",
        photoUrl = personPhotoUrl("coaches", coachId),
    )
}

// ----- Cache → Domínio -----

fun PlayerEntity.toDomain(): Player = Player(
    id = id,
    name = name,
    position = position,
    shirtNumber = shirtNumber,
    nationality = nationality,
    photoUrl = photoUrl,
    stats = PlayerStats(games = games, goals = goals, assists = assists),
)

fun CoachEntity.toDomain(): Coach = Coach(
    id = id,
    name = name,
    nationality = nationality,
    photoUrl = photoUrl,
)

fun TeamEntity.toDomain(players: List<Player>, coach: Coach?): Team = Team(
    id = id,
    name = name,
    shortName = shortName,
    crestUrl = crestUrl,
    colors = colors.split("|").filter { it.isNotEmpty() },
    venue = venue,
    victories = victories,
    players = players,
    coach = coach,
)

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
 * Monta a URL da foto na NOSSA API privada de fotos, indexada pelo id do
 * football-data.org (que continua sendo a fonte de dados). O parâmetro `name`
 * serve de fallback: se ainda não coletamos a foto daquele id, a API gera um
 * avatar de iniciais. A requisição é autenticada pelo header `x-api-key`
 * injetado no ImageLoader do Coil (ver AlbumApplication).
 */
private fun personPhotoUrl(kind: String, id: Int, name: String): String {
    val base = BuildConfig.PHOTO_API_BASE_URL.trimEnd('/')
    val encodedName = name.trim().replace(" ", "+")
    return "$base/$kind/$id?name=$encodedName"
}

/**
 * Quebra a string livre `clubColors` (ex.: "Sky Blue / White") em hex aproximados
 * conhecidos; mantém o texto original como fallback quando não há mapeamento.
 */
private fun parseColors(clubColors: String?): List<String> {
    if (clubColors.isNullOrBlank()) return listOf("#1E1E1E", "#FFFFFF")
    return clubColors.split("/", ",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

/**
 * Converte o DTO de equipe em entidade de cache, associando-a à sua competição.
 * O número de vitórias é estimado pelo ano de fundação como selo simbólico, já
 * que a API não expõe títulos diretamente.
 */
fun TeamDto.toEntity(competitionId: Int): TeamEntity = TeamEntity(
    id = id,
    competitionId = competitionId,
    name = name,
    shortName = shortName ?: name,
    crestUrl = crest ?: PLACEHOLDER_CREST,
    colors = parseColors(clubColors).joinToString("|"),
    description = venue?.let { "Manda seus jogos em $it." }.orEmpty(),
    victories = founded?.let { ((2026 - it) / 25).coerceIn(0, 6) } ?: 0,
)

/** Converte um jogador do `squad` em entidade de cache (estatísticas zeradas). */
fun PersonDto.toPlayerEntity(teamId: Int): PlayerEntity = PlayerEntity(
    id = id,
    teamId = teamId,
    name = name,
    position = position ?: "Indefinido",
    shirtNumber = shirtNumber ?: 0,
    nationality = nationality ?: "—",
    photoUrl = personPhotoUrl("players", id, name),
    games = 0,
    goals = 0,
    assists = 0,
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
        photoUrl = personPhotoUrl("coaches", coachId, coachName),
        profile = "Treinador da equipe.",
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
    profile = profile,
)

fun TeamEntity.toDomain(players: List<Player>, coach: Coach?): Team = Team(
    id = id,
    name = name,
    shortName = shortName,
    crestUrl = crestUrl,
    colors = colors.split("|").filter { it.isNotEmpty() },
    description = description,
    victories = victories,
    players = players,
    coach = coach,
)

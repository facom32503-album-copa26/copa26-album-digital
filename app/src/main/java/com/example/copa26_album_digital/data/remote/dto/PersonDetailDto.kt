package com.example.copa26_album_digital.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO da resposta de `GET /v4/persons/{id}` (detalhe de jogador ou treinador).
 */
@JsonClass(generateAdapter = true)
data class PersonDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "position") val position: String?,
    @Json(name = "dateOfBirth") val dateOfBirth: String?,
    @Json(name = "nationality") val nationality: String?,
    @Json(name = "shirtNumber") val shirtNumber: Int?,
    @Json(name = "currentTeam") val currentTeam: CurrentTeamDto?,
)

/**
 * DTO do time atual de uma pessoa, usado para contextualizar o detalhe.
 */
@JsonClass(generateAdapter = true)
data class CurrentTeamDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
)

/**
 * DTO da resposta de `GET /v4/competitions/{id}/scorers`.
 *
 * Usado para enriquecer as estatísticas dos jogadores (gols/assistências), já
 * que o endpoint de squad não traz números de desempenho.
 */
@JsonClass(generateAdapter = true)
data class ScorersDto(
    @Json(name = "scorers") val scorers: List<ScorerDto>,
)

/**
 * DTO de um artilheiro com suas estatísticas agregadas na temporada.
 */
@JsonClass(generateAdapter = true)
data class ScorerDto(
    @Json(name = "player") val player: PersonDto,
    @Json(name = "playedMatches") val playedMatches: Int?,
    @Json(name = "goals") val goals: Int?,
    @Json(name = "assists") val assists: Int?,
)

/** `aggregations` é omitido de propósito: no plano gratuito vem como String, não objeto. */
@JsonClass(generateAdapter = true)
data class PersonMatchesDto(
    @Json(name = "resultSet") val resultSet: ResultSetDto,
)

@JsonClass(generateAdapter = true)
data class ResultSetDto(
    @Json(name = "count") val count: Int,
)

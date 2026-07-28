package com.example.copa26_album_digital.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO da resposta de `GET /v4/competitions/{id}`.
 *
 * Reflete o JSON cru da API football-data.org. Os nomes seguem o contrato da API
 * e são convertidos para os modelos de domínio na camada de mapeamento.
 */
@JsonClass(generateAdapter = true)
data class CompetitionDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "code") val code: String,
    @Json(name = "emblem") val emblem: String?,
    @Json(name = "currentSeason") val currentSeason: SeasonDto?,
)

/**
 * DTO de uma temporada. Usado para derivar a "edição" exibida na tela inicial.
 */
@JsonClass(generateAdapter = true)
data class SeasonDto(
    @Json(name = "startDate") val startDate: String?,
    @Json(name = "endDate") val endDate: String?,
)

/**
 * DTO da resposta de `GET /v4/competitions/{id}/teams`.
 */
@JsonClass(generateAdapter = true)
data class CompetitionTeamsDto(
    @Json(name = "teams") val teams: List<TeamDto>,
)

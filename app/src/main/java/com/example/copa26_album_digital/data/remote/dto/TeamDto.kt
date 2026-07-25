package com.example.copa26_album_digital.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO da resposta de `GET /v4/teams/{id}`.
 *
 * Espelha o JSON da API football-data.org, incluindo o elenco (`squad`) e o
 * treinador (`coach`). Campos como cores oficiais vêm em texto livre (`clubColors`).
 */
@JsonClass(generateAdapter = true)
data class TeamDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "shortName") val shortName: String?,
    @Json(name = "crest") val crest: String?,
    @Json(name = "clubColors") val clubColors: String?,
    @Json(name = "founded") val founded: Int?,
    @Json(name = "venue") val venue: String?,
    @Json(name = "coach") val coach: CoachDto?,
    @Json(name = "squad") val squad: List<PersonDto>?,
)

/**
 * DTO de uma pessoa do elenco (jogador) retornada dentro de `squad`.
 */
@JsonClass(generateAdapter = true)
data class PersonDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "position") val position: String?,
    @Json(name = "dateOfBirth") val dateOfBirth: String?,
    @Json(name = "nationality") val nationality: String?,
    @Json(name = "shirtNumber") val shirtNumber: Int?,
)

/**
 * DTO do treinador retornado em `GET /v4/teams/{id}`.
 */
@JsonClass(generateAdapter = true)
data class CoachDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "nationality") val nationality: String?,
)

package com.example.copa26_album_digital.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.copa26_album_digital.data.remote.dto.CompetitionDto

/**
 * DTO da resposta de `GET /v4/competitions`, que envolve a lista em `competitions`.
 */
@JsonClass(generateAdapter = true)
data class CompetitionsResponse(
    @Json(name = "competitions") val competitions: List<CompetitionDto>,
)

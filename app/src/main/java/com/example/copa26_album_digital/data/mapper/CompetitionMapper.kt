package com.example.copa26_album_digital.data.mapper

import com.example.copa26_album_digital.data.local.entity.CompetitionEntity
import com.example.copa26_album_digital.data.remote.dto.CompetitionDto
import com.example.copa26_album_digital.domain.model.Competition
import com.example.copa26_album_digital.domain.model.Team

private const val PLACEHOLDER_EMBLEM = "https://via.placeholder.com/150?text=Trofeu"

/**
 * Converte o DTO de competição em entidade de cache. A "edição" é derivada do
 * ano inicial da temporada atual, quando disponível.
 */
fun CompetitionDto.toEntity(): CompetitionEntity = CompetitionEntity(
    id = id,
    name = name,
    code = code,
    edition = currentSeason?.startDate?.take(4) ?: "—",
    emblemUrl = emblem ?: PLACEHOLDER_EMBLEM,
)

/**
 * Reconstrói o modelo de domínio de competição a partir do cache, já anexando a
 * lista de equipes previamente mapeadas.
 */
fun CompetitionEntity.toDomain(teams: List<Team>): Competition = Competition(
    id = id,
    name = name,
    code = code,
    edition = edition,
    emblemUrl = emblemUrl,
    teams = teams,
)

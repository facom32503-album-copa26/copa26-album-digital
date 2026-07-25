package com.example.copa26_album_digital.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room que persiste uma equipe vinculada a uma competição.
 *
 * Sem chaves estrangeiras (apenas índice por `competitionId`) para manter o
 * cache simples e tolerante a sincronizações parciais.
 */
@Entity(
    tableName = "teams",
    indices = [Index(value = ["competitionId"])],
)
data class TeamEntity(
    @PrimaryKey val id: Int,
    val competitionId: Int,
    val name: String,
    val shortName: String,
    val crestUrl: String,
    val colors: String,
    val description: String,
    val victories: Int,
)

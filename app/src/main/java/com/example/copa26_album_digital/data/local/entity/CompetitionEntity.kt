package com.example.copa26_album_digital.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que persiste uma competição no cache local, permitindo abrir o
 * álbum offline sem nova requisição à API.
 */
@Entity(tableName = "competitions")
data class CompetitionEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val code: String,
    val edition: String,
    val emblemUrl: String,
)

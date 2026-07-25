package com.example.copa26_album_digital.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room que persiste um jogador pertencente a uma equipe.
 */
@Entity(
    tableName = "players",
    indices = [Index(value = ["teamId"])],
)
data class PlayerEntity(
    @PrimaryKey val id: Int,
    val teamId: Int,
    val name: String,
    val position: String,
    val shirtNumber: Int,
    val nationality: String,
    val photoUrl: String,
    val games: Int,
    val goals: Int,
    val assists: Int,
)

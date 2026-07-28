package com.example.copa26_album_digital.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que persiste o treinador de uma equipe. A chave primária é o
 * próprio `teamId`, pois há no máximo um treinador por equipe no cache.
 */
@Entity(tableName = "coaches")
data class CoachEntity(
    @PrimaryKey val teamId: Int,
    val id: Int,
    val name: String,
    val nationality: String,
    val photoUrl: String,
)

package com.example.copa26_album_digital.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.copa26_album_digital.data.local.dao.AlbumDao
import com.example.copa26_album_digital.data.local.entity.CoachEntity
import com.example.copa26_album_digital.data.local.entity.CompetitionEntity
import com.example.copa26_album_digital.data.local.entity.PlayerEntity
import com.example.copa26_album_digital.data.local.entity.TeamEntity

/**
 * Banco de dados Room que materializa o cache local do álbum (estratégia
 * offline-first). Versão incrementada a cada mudança de schema.
 */
@Database(
    entities = [
        CompetitionEntity::class,
        TeamEntity::class,
        PlayerEntity::class,
        CoachEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AlbumDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    companion object {
        const val DATABASE_NAME = "album_figurinhas.db"
    }
}

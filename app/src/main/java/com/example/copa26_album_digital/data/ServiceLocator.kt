package com.example.copa26_album_digital.data

import android.content.Context
import androidx.room.Room
import com.example.copa26_album_digital.BuildConfig
import com.example.copa26_album_digital.data.local.AlbumDatabase
import com.example.copa26_album_digital.data.remote.NetworkModule
import com.example.copa26_album_digital.data.repository.AlbumRepositoryImpl
import com.example.copa26_album_digital.domain.repository.AlbumRepository

/**
 * Localizador de serviços minimalista que monta a camada de dados (API + Room +
 * repositório). Em um projeto maior isto seria substituído por Hilt/Koin, mas
 * aqui mantém o exemplo autocontido e fácil de inspecionar.
 */
object ServiceLocator {

    @Volatile
    private var repository: AlbumRepository? = null

    fun provideRepository(context: Context): AlbumRepository =
        repository ?: synchronized(this) {
            repository ?: createRepository(context).also { repository = it }
        }

    private fun createRepository(context: Context): AlbumRepository {
        val api = NetworkModule.provideFootballApi(
            apiToken = BuildConfig.FOOTBALL_API_TOKEN,
            enableLogging = BuildConfig.DEBUG,
        )
        val database = Room.databaseBuilder(
            context.applicationContext,
            AlbumDatabase::class.java,
            AlbumDatabase.DATABASE_NAME,
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

        return AlbumRepositoryImpl(api = api, dao = database.albumDao())
    }
}

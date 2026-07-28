package com.example.copa26_album_digital.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.copa26_album_digital.data.local.entity.CoachEntity
import com.example.copa26_album_digital.data.local.entity.CompetitionEntity
import com.example.copa26_album_digital.data.local.entity.PlayerEntity
import com.example.copa26_album_digital.data.local.entity.TeamEntity

/**
 * DAO único do cache local do álbum. Concentra as operações de leitura e escrita
 * das quatro entidades, mantendo a sincronização do repositório simples.
 */
@Dao
interface AlbumDao {

    // ----- Escrita (upsert em lote durante a sincronização) -----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompetitions(competitions: List<CompetitionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTeams(teams: List<TeamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayers(players: List<PlayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoach(coach: CoachEntity)

    // ----- Leitura -----

    @Query("SELECT * FROM competitions")
    suspend fun getCompetitions(): List<CompetitionEntity>

    @Query("SELECT * FROM competitions WHERE id = :competitionId LIMIT 1")
    suspend fun getCompetition(competitionId: Int): CompetitionEntity?

    @Query("SELECT * FROM teams WHERE competitionId = :competitionId ORDER BY name")
    suspend fun getTeamsByCompetition(competitionId: Int): List<TeamEntity>

    @Query("SELECT * FROM teams WHERE id = :teamId LIMIT 1")
    suspend fun getTeam(teamId: Int): TeamEntity?

    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY shirtNumber")
    suspend fun getPlayersByTeam(teamId: Int): List<PlayerEntity>

    @Query("SELECT * FROM players WHERE id = :playerId LIMIT 1")
    suspend fun getPlayer(playerId: Int): PlayerEntity?

    @Query("SELECT * FROM coaches WHERE teamId = :teamId LIMIT 1")
    suspend fun getCoachByTeam(teamId: Int): CoachEntity?
}

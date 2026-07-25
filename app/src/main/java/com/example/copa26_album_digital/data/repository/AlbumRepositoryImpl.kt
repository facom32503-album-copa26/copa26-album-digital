package com.example.copa26_album_digital.data.repository

import com.example.copa26_album_digital.data.local.SquadSeed
import com.example.copa26_album_digital.data.local.dao.AlbumDao
import com.example.copa26_album_digital.data.mapper.toDomain
import com.example.copa26_album_digital.data.mapper.toEntity
import com.example.copa26_album_digital.data.mapper.toPlayerEntity
import com.example.copa26_album_digital.data.remote.FootballApi
import com.example.copa26_album_digital.domain.model.Competition
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.domain.repository.AlbumRepository
import com.example.copa26_album_digital.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Implementação offline-first do [AlbumRepository].
 *
 * Estratégia de sincronização: tenta buscar da API remota e atualizar o cache
 * (Room). Em caso de falha de rede, recai sobre os dados já persistidos. Assim a
 * UI sempre recebe dados quando o cache existe, mesmo offline.
 */
class AlbumRepositoryImpl(
    private val api: FootballApi,
    private val dao: AlbumDao,
    private val squadSeed: SquadSeed = SquadSeed(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AlbumRepository {

    override suspend fun getCompetitions(): Result<List<Competition>> =
        withContext(ioDispatcher) {
            runCatching {
                val remote = api.getCompetitions().competitions.map { it.toEntity() }
                dao.upsertCompetitions(remote)
            }.onFailureLog()

            val cached = dao.getCompetitions()
            if (cached.isEmpty()) {
                Result.Error("Nenhuma competição disponível (sem rede e sem cache).")
            } else {
                Result.Success(cached.map { it.toDomain(teams = emptyList()) })
            }
        }

    override suspend fun getCompetition(code: String): Result<Competition> =
        withContext(ioDispatcher) {
            runCatching { syncCompetition(code) }.onFailureLog()

            val competition = dao.getCompetitions().firstOrNull { it.code == code }
                ?: return@withContext Result.Error("Competição '$code' não encontrada.")

            val teams = dao.getTeamsByCompetition(competition.id).map { teamEntity ->
                val players = dao.getPlayersByTeam(teamEntity.id).map { it.toDomain() }
                val coach = dao.getCoachByTeam(teamEntity.id)?.toDomain()
                teamEntity.toDomain(players = players, coach = coach)
            }
            Result.Success(competition.toDomain(teams = teams))
        }

    override suspend fun getTeam(teamId: Int): Result<Team> =
        withContext(ioDispatcher) {
            runCatching { syncTeam(teamId) }.onFailureLog()

            val teamEntity = dao.getTeam(teamId)
                ?: return@withContext Result.Error("Equipe $teamId não encontrada.")

            val players = dao.getPlayersByTeam(teamId).map { it.toDomain() }
            val coach = dao.getCoachByTeam(teamId)?.toDomain()
            Result.Success(teamEntity.toDomain(players = players, coach = coach))
        }

    override suspend fun getPlayer(playerId: Int): Result<Player> =
        withContext(ioDispatcher) {
            val cached = dao.getPlayer(playerId)
                ?: return@withContext Result.Error("Jogador $playerId não está no cache.")
            Result.Success(cached.toDomain())
        }

    // ----- Sincronização (rede → cache) -----

    private suspend fun syncCompetition(code: String) {
        val competition = api.getCompetition(code).toEntity()
        dao.upsertCompetitions(listOf(competition))

        val teams = api.getCompetitionTeams(code).teams
        dao.upsertTeams(teams.map { it.toEntity(competition.id) })

        // Em competições de seleções (ex.: Copa do Mundo), a lista de equipes já
        // traz o elenco e o treinador inline — persistimos de imediato.
        teams.forEach { team ->
            team.coach?.toEntity(team.id)?.let { dao.upsertCoach(it) }
            val players = team.squad.orEmpty().map { it.toPlayerEntity(team.id) }
            if (players.isNotEmpty()) dao.upsertPlayers(players)
        }
    }

    private suspend fun syncTeam(teamId: Int) {
        val team = api.getTeam(teamId)

        // Plano gratuito não expõe coach/squad: usa o seed como fallback.
        val coach = team.coach?.toEntity(teamId) ?: squadSeed.coachFor(teamId)
        coach?.let { dao.upsertCoach(it) }

        val remotePlayers = team.squad.orEmpty().map { it.toPlayerEntity(teamId) }
        val players = remotePlayers.ifEmpty { squadSeed.playersFor(teamId) }
        if (players.isNotEmpty()) dao.upsertPlayers(players)
    }

    /** Loga falhas de sincronização sem propagá-las, preservando o modo offline. */
    private fun <T> kotlin.Result<T>.onFailureLog(): kotlin.Result<T> = onFailure { error ->
        if (error !is IOException && error !is retrofit2.HttpException) throw error
    }
}

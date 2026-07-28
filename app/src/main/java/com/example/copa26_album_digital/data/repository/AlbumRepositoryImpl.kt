package com.example.copa26_album_digital.data.repository

import android.util.Log
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
            val syncError = runCatching {
                val remote = api.getCompetitions().competitions.map { it.toEntity() }
                dao.upsertCompetitions(remote)
            }.onFailureLog().exceptionOrNull()

            val cached = dao.getCompetitions()
            if (cached.isEmpty()) {
                Result.Error(syncError.toCacheMissMessage("Nenhuma competição disponível"), syncError)
            } else {
                Result.Success(cached.map { it.toDomain(teams = emptyList()) })
            }
        }

    override suspend fun getCompetition(code: String): Result<Competition> =
        withContext(ioDispatcher) {
            val syncError = runCatching { syncCompetition(code) }.onFailureLog().exceptionOrNull()

            val competition = dao.getCompetitions().firstOrNull { it.code == code }
                ?: return@withContext Result.Error(
                    syncError.toCacheMissMessage("Competição '$code' não encontrada"),
                    syncError,
                )

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

        // Fallback para quando a API não retornar coach/squad (rate limit ou instabilidade).
        val coach = team.coach?.toEntity(teamId) ?: squadSeed.coachFor(teamId)
        coach?.let { dao.upsertCoach(it) }

        val remotePlayers = team.squad.orEmpty().map { it.toPlayerEntity(teamId) }
        val players = remotePlayers.ifEmpty { squadSeed.playersFor(teamId) }
        if (players.isNotEmpty()) dao.upsertPlayers(players)
    }

    /** Loga falhas de sincronização sem propagá-las, preservando o modo offline. */
    private fun <T> kotlin.Result<T>.onFailureLog(): kotlin.Result<T> = onFailure { error ->
        if (error !is IOException && error !is retrofit2.HttpException) throw error
        Log.w(TAG, "Falha ao sincronizar com a API remota, mantendo cache local", error)
    }

    /**
     * Traduz a causa raiz de uma falha de sincronização (quando não há cache para
     * cobri-la) em uma mensagem acionável, em vez do genérico "não encontrada" —
     * que escondia problemas de configuração (ex.: token ausente) e de rede.
     */
    private fun Throwable?.toCacheMissMessage(prefix: String): String = when {
        this is retrofit2.HttpException && (code() == 400 || code() == 401 || code() == 403) ->
            "$prefix: falha de autenticação na API (verifique FOOTBALL_API_TOKEN em local.properties)."
        this is retrofit2.HttpException -> "$prefix: a API respondeu com erro HTTP ${code()}."
        this is IOException -> "$prefix: sem conexão com a internet e sem cache local."
        else -> "$prefix."
    }

    private companion object {
        const val TAG = "AlbumRepository"
    }
}

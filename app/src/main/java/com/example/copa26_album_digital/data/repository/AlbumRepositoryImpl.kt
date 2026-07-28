package com.example.copa26_album_digital.data.repository

import android.util.Log
import com.example.copa26_album_digital.data.local.dao.AlbumDao
import com.example.copa26_album_digital.data.local.entity.PlayerEntity
import com.example.copa26_album_digital.data.local.entity.hasStats
import com.example.copa26_album_digital.data.mapper.toDomain
import com.example.copa26_album_digital.data.mapper.toEntity
import com.example.copa26_album_digital.data.mapper.toPlayerEntity
import com.example.copa26_album_digital.data.remote.FootballApi
import com.example.copa26_album_digital.data.remote.dto.PersonDto
import com.example.copa26_album_digital.data.remote.dto.ScorerDto
import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Competition
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.domain.repository.AlbumRepository
import com.example.copa26_album_digital.domain.util.Result
import kotlinx.coroutines.CancellationException
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
            val player = if (cached.games > 0) cached else syncPlayerGames(cached)
            Result.Success(player.toDomain())
        }

    override suspend fun getCoach(teamId: Int): Result<Coach> =
        withContext(ioDispatcher) {
            val cached = dao.getCoachByTeam(teamId)
                ?: return@withContext Result.Error("Equipe $teamId não tem técnico no cache.")
            Result.Success(cached.toDomain())
        }

    // ----- Sincronização (rede → cache) -----

    private suspend fun syncCompetition(code: String) {
        val competition = api.getCompetition(code).toEntity()
        dao.upsertCompetitions(listOf(competition))

        val teams = api.getCompetitionTeams(code).teams
        dao.upsertTeams(teams.map { it.toEntity(competition.id) })

        val statsByPlayerId = fetchScorers(code)

        // Em competições de seleções (ex.: Copa do Mundo), a lista de equipes já
        // traz o elenco e o treinador inline — persistimos de imediato.
        teams.forEach { team ->
            team.coach?.toEntity(team.id)?.let { dao.upsertCoach(it) }
            persistSquad(teamId = team.id, squad = team.squad.orEmpty(), statsByPlayerId = statsByPlayerId)
        }
    }

    private suspend fun syncTeam(teamId: Int) {
        val team = api.getTeam(teamId)

        team.coach?.toEntity(teamId)?.let { dao.upsertCoach(it) }

        // Evita gastar requisição no ranking se o cache deste time já tem estatísticas (limite: 10 req/min).
        val statsByPlayerId = if (dao.getPlayersByTeam(teamId).any { it.hasStats }) {
            emptyMap()
        } else {
            competitionCodeOf(teamId)?.let { fetchScorers(it) }.orEmpty()
        }

        persistSquad(teamId = teamId, squad = team.squad.orEmpty(), statsByPlayerId = statsByPlayerId)
    }

    /** Conta os jogos do jogador (o ranking só cobre quem marcou gol); custa 1 requisição, então é sob demanda. */
    private suspend fun syncPlayerGames(player: PlayerEntity): PlayerEntity {
        val code = competitionCodeOf(player.teamId) ?: return player
        val games = runCatching { api.getPersonMatches(player.id, code).resultSet.count }
            .onFailureLog()
            .getOrNull()
            ?.takeIf { it > 0 }
            ?: return player

        return player.copy(games = games).also { dao.upsertPlayers(listOf(it)) }
    }

    /** Código da competição a que a equipe pertence, resolvido pelo cache. */
    private suspend fun competitionCodeOf(teamId: Int): String? =
        dao.getTeam(teamId)?.competitionId?.let { compId ->
            dao.getCompetitions().firstOrNull { it.id == compId }?.code
        }

    /** Ranking de artilheiros indexado por id do jogador; falha devolve mapa vazio. */
    private suspend fun fetchScorers(code: String): Map<Int, ScorerDto> =
        runCatching { api.getScorers(code).scorers }
            .onFailure { Log.w(TAG, "Falha ao buscar artilheiros para enriquecer estatísticas", it) }
            .getOrDefault(emptyList())
            .associateBy { it.player.id }

    /** Persiste o elenco; como o upsert é REPLACE, mantém a estatística em cache de quem não está no ranking. */
    private suspend fun persistSquad(
        teamId: Int,
        squad: List<PersonDto>,
        statsByPlayerId: Map<Int, ScorerDto>,
    ) {
        if (squad.isEmpty()) return
        val cachedById = dao.getPlayersByTeam(teamId).associateBy { it.id }
        val players = squad.map { person ->
            val stats = statsByPlayerId[person.id]
            val cached = cachedById[person.id]
            person.toPlayerEntity(
                teamId = teamId,
                games = stats?.playedMatches ?: cached?.games ?: 0,
                goals = stats?.goals ?: cached?.goals ?: 0,
                assists = stats?.assists ?: cached?.assists ?: 0,
            )
        }
        dao.upsertPlayers(players)
    }

    /** Loga falhas de sincronização sem propagá-las; só [CancellationException] sobe, para não quebrar o cancelamento. */
    private fun <T> kotlin.Result<T>.onFailureLog(): kotlin.Result<T> = onFailure { error ->
        if (error is CancellationException) throw error
        Log.w(TAG, "Falha ao sincronizar com a API remota, mantendo cache local", error)
    }

    /** Traduz a causa raiz da falha em mensagem acionável, em vez do genérico "não encontrada". */
    private fun Throwable?.toCacheMissMessage(prefix: String): String {
        val error = this ?: return "$prefix."
        return when (error) {
            is retrofit2.HttpException -> when (error.code()) {
                400, 401, 403 ->
                    "$prefix: falha de autenticação na API (verifique FOOTBALL_API_TOKEN em local.properties)."
                429 ->
                    "$prefix: limite de requisições da API atingido (10 por minuto no plano gratuito). Tente de novo em instantes."
                else -> "$prefix: a API respondeu com erro HTTP ${error.code()}."
            }
            is IOException -> "$prefix: sem conexão com a internet e sem cache local."
            else -> "$prefix: resposta inesperada da API (${error.javaClass.simpleName})."
        }
    }

    private companion object {
        const val TAG = "AlbumRepository"
    }
}

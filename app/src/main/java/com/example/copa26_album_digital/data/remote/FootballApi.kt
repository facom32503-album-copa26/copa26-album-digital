package com.example.copa26_album_digital.data.remote

import com.example.copa26_album_digital.data.remote.dto.CompetitionDto
import com.example.copa26_album_digital.data.remote.dto.CompetitionTeamsDto
import com.example.copa26_album_digital.data.remote.dto.PersonDetailDto
import com.example.copa26_album_digital.data.remote.dto.PersonMatchesDto
import com.example.copa26_album_digital.data.remote.dto.ScorersDto
import com.example.copa26_album_digital.data.remote.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato Retrofit para a API REST football-data.org (v4).
 *
 * O token de autenticação é injetado automaticamente pelo [AuthInterceptor], por
 * isso nenhum método precisa recebê-lo como parâmetro.
 */
interface FootballApi {

    /** Lista todas as competições disponíveis (tela inicial). */
    @GET("v4/competitions")
    suspend fun getCompetitions(): CompetitionsResponse

    /** Detalha uma competição pelo seu código (ex.: "PL", "BSA", "CL"). */
    @GET("v4/competitions/{code}")
    suspend fun getCompetition(@Path("code") code: String): CompetitionDto

    /** Lista as equipes participantes de uma competição (cards da tela inicial). */
    @GET("v4/competitions/{code}/teams")
    suspend fun getCompetitionTeams(@Path("code") code: String): CompetitionTeamsDto

    /** Detalha uma equipe e seu elenco (TeamScreen). */
    @GET("v4/teams/{id}")
    suspend fun getTeam(@Path("id") id: Int): TeamDto

    /** Detalha uma pessoa — jogador ou treinador (PlayerDetailScreen). */
    @GET("v4/persons/{id}")
    suspend fun getPerson(@Path("id") id: Int): PersonDetailDto

    /** Lista os artilheiros de uma competição para enriquecer estatísticas. */
    @GET("v4/competitions/{code}/scorers")
    suspend fun getScorers(
        @Path("code") code: String,
        @Query("limit") limit: Int = ALL_SCORERS_LIMIT,
    ): ScorersDto

    /**
     * Partidas de um jogador na competição. Serve para contar jogos disputados de
     * quem não aparece no ranking de artilheiros (goleiros, defensores).
     *
     * `limit` precisa cobrir todas as partidas: `resultSet.count` reflete o
     * conjunto paginado, então um limite baixo devolve um total menor que o real.
     */
    @GET("v4/persons/{id}/matches")
    suspend fun getPersonMatches(
        @Path("id") id: Int,
        @Query("competitions") competitions: String,
        @Query("limit") limit: Int = MAX_PAGE_LIMIT,
    ): PersonMatchesDto

    companion object {
        /** Acima do total de artilheiros de uma Copa (179 em 2026), para trazer a lista inteira. */
        const val ALL_SCORERS_LIMIT = 200

        /** Teto de paginação aceito pela API. */
        const val MAX_PAGE_LIMIT = 100
    }
}

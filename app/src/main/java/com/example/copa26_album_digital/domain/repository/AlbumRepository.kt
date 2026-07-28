package com.example.copa26_album_digital.domain.repository

import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Competition
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.domain.util.Result

/**
 * Contrato do repositório do álbum. Abstrai a origem dos dados (API remota +
 * cache local) do restante do app, conforme a arquitetura MVVM.
 *
 * O ViewModel depende desta interface, nunca das implementações concretas.
 */
interface AlbumRepository {

    companion object {
        /**
         * Competição padrão do álbum: a Copa do Mundo FIFA 2026, alinhada ao tema
         * do projeto. Na API football-data.org seu código é "WC" e o endpoint de
         * equipes já retorna o elenco e o treinador de cada seleção inline.
         */
        const val DEFAULT_COMPETITION_CODE = "WC"
    }

    /** Lista as competições disponíveis para a tela inicial. */
    suspend fun getCompetitions(): Result<List<Competition>>

    /** Carrega uma competição com suas equipes (cards da tela inicial). */
    suspend fun getCompetition(code: String): Result<Competition>

    /** Carrega uma equipe com elenco e treinador (TeamScreen). */
    suspend fun getTeam(teamId: Int): Result<Team>

    /** Carrega o detalhe de um jogador a partir do cache (PlayerDetailScreen). */
    suspend fun getPlayer(playerId: Int): Result<Player>

    /** Carrega o técnico de uma equipe a partir do cache (PlayerDetailScreen). */
    suspend fun getCoach(teamId: Int): Result<Coach>
}

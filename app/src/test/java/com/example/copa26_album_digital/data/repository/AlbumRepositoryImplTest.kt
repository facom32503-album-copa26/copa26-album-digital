package com.example.copa26_album_digital.data.repository

import com.example.copa26_album_digital.data.local.dao.AlbumDao
import com.example.copa26_album_digital.data.local.entity.CompetitionEntity
import com.example.copa26_album_digital.data.local.entity.PlayerEntity
import com.example.copa26_album_digital.data.local.entity.TeamEntity
import com.example.copa26_album_digital.data.remote.FootballApi
import com.example.copa26_album_digital.data.remote.dto.PersonDto
import com.example.copa26_album_digital.data.remote.dto.PersonMatchesDto
import com.example.copa26_album_digital.data.remote.dto.ResultSetDto
import com.example.copa26_album_digital.data.remote.dto.ScorerDto
import com.example.copa26_album_digital.data.remote.dto.ScorersDto
import com.example.copa26_album_digital.data.remote.dto.TeamDto
import com.example.copa26_album_digital.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Testes funcionais da estratégia offline-first do repositório e, principalmente,
 * do enriquecimento de estatísticas de jogadores pelo endpoint de artilheiros.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AlbumRepositoryImplTest {

    private val api = mockk<FootballApi>(relaxed = true)
    private val dao = mockk<AlbumDao>(relaxed = true)

    private fun repository() = AlbumRepositoryImpl(
        api = api,
        dao = dao,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    private fun teamEntity(id: Int, competitionId: Int) = TeamEntity(
        id = id,
        competitionId = competitionId,
        name = "Argentina",
        shortName = "Argentina",
        crestUrl = "url",
        colors = "Sky Blue|White",
        venue = "",
        victories = 3,
    )

    @Test
    fun `getCompetition retorna erro sem rede e sem cache`() = runTest {
        coEvery { api.getCompetition(any()) } throws IOException("offline")
        coEvery { dao.getCompetitions() } returns emptyList()

        val result = repository().getCompetition("WC")

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getTeam usa cache quando a rede falha`() = runTest {
        coEvery { api.getTeam(any()) } throws IOException("offline")
        coEvery { dao.getTeam(762) } returns teamEntity(762, 2000)
        coEvery { dao.getPlayersByTeam(762) } returns emptyList()
        coEvery { dao.getCoachByTeam(762) } returns null

        val result = repository().getTeam(762)

        assertTrue(result is Result.Success)
        assertEquals(listOf("Sky Blue", "White"), (result as Result.Success).data.colors)
    }

    @Test
    fun `getTeam retorna erro quando equipe nao esta no cache`() = runTest {
        coEvery { api.getTeam(any()) } throws IOException("offline")
        coEvery { dao.getTeam(99) } returns null

        val result = repository().getTeam(99)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getTeam enriquece jogador com estatisticas reais dos artilheiros`() = runTest {
        // Elenco remoto com Messi (id 3218), sem estatísticas de desempenho.
        coEvery { api.getTeam(762) } returns TeamDto(
            id = 762,
            name = "Argentina",
            shortName = "Argentina",
            crest = null,
            clubColors = "Sky Blue / White",
            founded = 1893,
            venue = null,
            coach = null,
            squad = listOf(
                PersonDto(
                    id = 3218,
                    name = "Lionel Messi",
                    position = "Offence",
                    dateOfBirth = null,
                    nationality = "Argentina",
                    shirtNumber = 10,
                ),
            ),
        )
        // Competição em cache para descobrir o código usado no endpoint de artilheiros.
        coEvery { dao.getTeam(762) } returns teamEntity(762, 2000)
        coEvery { dao.getCompetitions() } returns listOf(
            CompetitionEntity(id = 2000, name = "FIFA World Cup", code = "WC", edition = "2026", emblemUrl = "url"),
        )
        // Artilheiros: Messi com 8 jogos, 8 gols e 4 assistências.
        coEvery { api.getScorers("WC") } returns ScorersDto(
            scorers = listOf(
                ScorerDto(
                    player = PersonDto(3218, "Lionel Messi", "Offence", null, "Argentina", 10),
                    playedMatches = 8,
                    goals = 8,
                    assists = 4,
                ),
            ),
        )
        coEvery { dao.getPlayersByTeam(762) } returns emptyList()
        coEvery { dao.getCoachByTeam(762) } returns null

        val captured = slot<List<PlayerEntity>>()
        coEvery { dao.upsertPlayers(capture(captured)) } returns Unit

        repository().getTeam(762)

        val messi = captured.captured.first { it.id == 3218 }
        assertEquals(8, messi.games)
        assertEquals(8, messi.goals)
        assertEquals(4, messi.assists)
    }

    @Test
    fun `getTeam mantem sincronizacao quando artilheiros falham`() = runTest {
        coEvery { api.getTeam(762) } returns TeamDto(
            id = 762,
            name = "Argentina",
            shortName = "Argentina",
            crest = null,
            clubColors = null,
            founded = 1893,
            venue = null,
            coach = null,
            squad = listOf(
                PersonDto(3218, "Lionel Messi", "Offence", null, "Argentina", 10),
            ),
        )
        coEvery { dao.getTeam(762) } returns teamEntity(762, 2000)
        coEvery { dao.getCompetitions() } returns listOf(
            CompetitionEntity(2000, "FIFA World Cup", "WC", "2026", "url"),
        )
        // Falha ao buscar artilheiros não deve quebrar a sincronização.
        coEvery { api.getScorers(any(), any()) } throws IOException("rate limit")
        coEvery { dao.getPlayersByTeam(762) } returns emptyList()
        coEvery { dao.getCoachByTeam(762) } returns null

        val captured = slot<List<PlayerEntity>>()
        coEvery { dao.upsertPlayers(capture(captured)) } returns Unit

        val result = repository().getTeam(762)

        assertTrue(result is Result.Success)
        val messi = captured.captured.first { it.id == 3218 }
        assertEquals(0, messi.goals) // sem artilheiros → estatísticas zeradas, sem crash
        coVerify { dao.upsertPlayers(any()) }
    }

    @Test
    fun `re-sincronizar nao apaga estatisticas ja gravadas no cache`() = runTest {
        // Regressão: o upsert REPLACE regravava 0/0/0 em quem não estava no ranking daquela resposta.
        coEvery { api.getTeam(762) } returns TeamDto(
            id = 762,
            name = "Argentina",
            shortName = "Argentina",
            crest = null,
            clubColors = null,
            founded = 1893,
            venue = null,
            coach = null,
            squad = listOf(
                PersonDto(3218, "Lionel Messi", "Offence", null, "Argentina", 10),
            ),
        )
        coEvery { dao.getTeam(762) } returns teamEntity(762, 2000)
        coEvery { dao.getCoachByTeam(762) } returns null
        // Messi já tem estatísticas em cache de uma sincronização anterior.
        coEvery { dao.getPlayersByTeam(762) } returns listOf(
            PlayerEntity(
                id = 3218,
                teamId = 762,
                name = "Lionel Messi",
                position = "Offence",
                shirtNumber = 10,
                nationality = "Argentina",
                photoUrl = "url",
                games = 8,
                goals = 8,
                assists = 4,
            ),
        )

        val captured = slot<List<PlayerEntity>>()
        coEvery { dao.upsertPlayers(capture(captured)) } returns Unit

        repository().getTeam(762)

        val messi = captured.captured.first { it.id == 3218 }
        assertEquals(8, messi.games)
        assertEquals(8, messi.goals)
        assertEquals(4, messi.assists)
        coVerify(exactly = 0) { api.getScorers(any(), any()) }
    }

    private fun playerEntity(id: Int, teamId: Int, games: Int) = PlayerEntity(
        id = id,
        teamId = teamId,
        name = "Emiliano Martínez",
        position = "Goalkeeper",
        shirtNumber = 23,
        nationality = "Argentina",
        photoUrl = "url",
        games = games,
        goals = 0,
        assists = 0,
    )

    @Test
    fun `getPlayer busca jogos de quem nao esta no ranking de artilheiros`() = runTest {
        // Goleiros e defensores nunca aparecem em /scorers: os jogos vêm de /persons/{id}/matches.
        coEvery { dao.getPlayer(6) } returns playerEntity(id = 6, teamId = 762, games = 0)
        coEvery { dao.getTeam(762) } returns teamEntity(762, 2000)
        coEvery { dao.getCompetitions() } returns listOf(
            CompetitionEntity(2000, "FIFA World Cup", "WC", "2026", "url"),
        )
        coEvery { api.getPersonMatches(6, "WC", any()) } returns PersonMatchesDto(ResultSetDto(count = 7))

        val result = repository().getPlayer(6)

        assertTrue(result is Result.Success)
        assertEquals(7, (result as Result.Success).data.stats.games)
        coVerify { dao.upsertPlayers(match { list -> list.single().games == 7 }) }
    }

    @Test
    fun `getPlayer nao gasta requisicao quando ja tem jogos em cache`() = runTest {
        coEvery { dao.getPlayer(6) } returns playerEntity(id = 6, teamId = 762, games = 7)

        val result = repository().getPlayer(6)

        assertEquals(7, (result as Result.Success).data.stats.games)
        coVerify(exactly = 0) { api.getPersonMatches(any(), any(), any()) }
    }

    @Test
    fun `getPlayer mantem o cache quando a busca de jogos falha`() = runTest {
        coEvery { dao.getPlayer(6) } returns playerEntity(id = 6, teamId = 762, games = 0)
        coEvery { dao.getTeam(762) } returns teamEntity(762, 2000)
        coEvery { dao.getCompetitions() } returns listOf(
            CompetitionEntity(2000, "FIFA World Cup", "WC", "2026", "url"),
        )
        coEvery { api.getPersonMatches(any(), any(), any()) } throws IOException("offline")

        val result = repository().getPlayer(6)

        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.stats.games)
    }
}

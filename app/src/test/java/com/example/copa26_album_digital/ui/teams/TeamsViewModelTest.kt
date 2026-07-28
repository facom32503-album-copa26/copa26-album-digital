package com.example.copa26_album_digital.ui.teams

import com.example.copa26_album_digital.domain.model.Competition
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.domain.repository.AlbumRepository
import com.example.copa26_album_digital.domain.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Testes funcionais da camada de apresentação (MVVM).
 *
 * Verificam que o [TeamsViewModel] consome o repositório e projeta o resultado
 * no [TeamsUiState] observado pela UI (Compose) — cobrindo sucesso e erro.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TeamsViewModelTest {

    private val repository = mockk<AlbumRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun team(id: Int, name: String) = Team(
        id = id,
        name = name,
        shortName = name,
        crestUrl = "url",
        colors = listOf("Sky Blue", "White"),
        description = "",
        victories = 3,
        players = emptyList(),
        coach = null,
    )

    @Test
    fun `carrega equipes com sucesso e expoe no estado`() {
        val teams = listOf(team(762, "Argentina"), team(764, "Brazil"))
        coEvery { repository.getCompetition(any()) } returns Result.Success(
            Competition(
                id = 2000,
                name = "FIFA World Cup",
                code = "WC",
                edition = "2026",
                emblemUrl = "url",
                teams = teams,
            ),
        )

        val viewModel = TeamsViewModel(repository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(teams, state.teams)
    }

    @Test
    fun `erro do repositorio vira mensagem no estado`() {
        coEvery { repository.getCompetition(any()) } returns Result.Error("sem conexão")

        val viewModel = TeamsViewModel(repository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("sem conexão", state.errorMessage)
    }
}

package com.example.copa26_album_digital.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.domain.repository.AlbumRepository
import com.example.copa26_album_digital.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado da tela de detalhe da equipe: carregamento, dados ou erro. */
data class TeamUiState(
    val isLoading: Boolean = true,
    val team: Team? = null,
    val errorMessage: String? = null,
)

/**
 * ViewModel do detalhe da equipe. Recebe o identificador da equipe pela rota e
 * carrega elenco e treinador do repositório de forma assíncrona.
 */
class TeamViewModel(
    private val repository: AlbumRepository,
    private val teamId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamUiState())
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = TeamUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getTeam(teamId)) {
                is Result.Success -> _uiState.value = TeamUiState(
                    isLoading = false,
                    team = result.data,
                )
                is Result.Error -> _uiState.value = TeamUiState(
                    isLoading = false,
                    errorMessage = result.message,
                )
            }
        }
    }

    companion object {
        fun provideFactory(repository: AlbumRepository, teamId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TeamViewModel(repository, teamId) as T
            }
    }
}

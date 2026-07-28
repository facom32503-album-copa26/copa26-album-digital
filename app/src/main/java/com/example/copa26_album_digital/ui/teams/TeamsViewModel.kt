package com.example.copa26_album_digital.ui.teams

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

/** Estado da tela com a grade de equipes: carregamento, dados ou erro. */
data class TeamsUiState(
    val isLoading: Boolean = true,
    val teams: List<Team> = emptyList(),
    val errorMessage: String? = null,
)

/**
 * ViewModel da lista de equipes. Carrega a competição padrão (Copa do Mundo) do
 * repositório de forma assíncrona e expõe o estado via [StateFlow], mantendo a
 * View "burra" conforme a arquitetura MVVM.
 */
class TeamsViewModel(
    private val repository: AlbumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamsUiState())
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = TeamsUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getCompetition(AlbumRepository.DEFAULT_COMPETITION_CODE)) {
                is Result.Success -> _uiState.value = TeamsUiState(
                    isLoading = false,
                    teams = result.data.teams,
                )
                is Result.Error -> _uiState.value = TeamsUiState(
                    isLoading = false,
                    errorMessage = result.message,
                )
            }
        }
    }

    companion object {
        fun provideFactory(repository: AlbumRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TeamsViewModel(repository) as T
            }
    }
}

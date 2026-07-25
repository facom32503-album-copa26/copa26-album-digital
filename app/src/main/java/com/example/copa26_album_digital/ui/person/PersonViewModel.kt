package com.example.copa26_album_digital.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.repository.AlbumRepository
import com.example.copa26_album_digital.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado da tela de detalhe: pode representar um jogador OU o técnico.
 * Apenas um dos campos é preenchido por vez.
 */
data class PersonUiState(
    val isLoading: Boolean = true,
    val player: Player? = null,
    val coach: Coach? = null,
    val errorMessage: String? = null,
)

/**
 * ViewModel do detalhe de jogador/técnico. Recebe o id da equipe e da pessoa
 * pela rota e resolve, no repositório, qual entidade exibir.
 */
class PersonViewModel(
    private val repository: AlbumRepository,
    private val teamId: Int,
    private val personId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonUiState())
    val uiState: StateFlow<PersonUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = PersonUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getTeam(teamId)) {
                is Result.Success -> {
                    val team = result.data
                    val player = team.players.firstOrNull { it.id == personId }
                    val coach = team.coach?.takeIf { it.id == personId }
                    // Sem jogador nem técnico: deixa player/coach nulos e a View
                    // exibe a mensagem localizada (person_not_found).
                    _uiState.value = PersonUiState(
                        isLoading = false,
                        player = player,
                        coach = coach,
                    )
                }
                is Result.Error -> _uiState.value = PersonUiState(
                    isLoading = false,
                    errorMessage = result.message,
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: AlbumRepository,
            teamId: Int,
            personId: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PersonViewModel(repository, teamId, personId) as T
            }
    }
}

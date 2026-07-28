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
            // Lê do cache: a equipe já gravou elenco e técnico, e re-sincronizar custaria 2 requisições.
            val playerResult = repository.getPlayer(personId)
            if (playerResult is Result.Success) {
                _uiState.value = PersonUiState(isLoading = false, player = playerResult.data)
                return@launch
            }

            // Sem jogador com esse id: pode ser o técnico. Se não for, a View exibe person_not_found.
            val coachResult = repository.getCoach(teamId)
            val coach = if (coachResult is Result.Success && coachResult.data.id == personId) {
                coachResult.data
            } else {
                null
            }
            _uiState.value = PersonUiState(isLoading = false, coach = coach)
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

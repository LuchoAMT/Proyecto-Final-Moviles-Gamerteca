package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetGameDetailsUseCase
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetPopularGamesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val getGameDetails: GetGameDetailsUseCase,
    private val getPopularGames: GetPopularGamesUseCase
) : ViewModel() {
    // Estado para juegos relacionados
    private val _relatedGames = MutableStateFlow<List<GameModel>>(emptyList())
    val relatedGames: StateFlow<List<GameModel>> = _relatedGames.asStateFlow()

    sealed class UiState {
        object Init : UiState()
        object Loading : UiState()
        data class Success(val game: GameModel) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Init)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun loadGameDetails(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = UiState.Loading
            val result = getGameDetails(gameId)
            result.fold(
                onSuccess = { game ->
                    _state.value = UiState.Success(game)
                },
                onFailure = { exception ->
                    _state.value = UiState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadRelatedGames(gameIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            val games = mutableListOf<GameModel>()
            gameIds.forEach { id ->
                getGameDetails(id).onSuccess { game ->
                    games.add(game)
                }
            }
            _relatedGames.value = games
        }
    }
}
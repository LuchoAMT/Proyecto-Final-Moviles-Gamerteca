package com.ucb.proyectofinalgamerteca.features.games.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetPopularGamesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GamesListViewModel(
    private val getPopularGames: GetPopularGamesUseCase
) : ViewModel() {
    sealed class UiState {
        object Init : UiState()
        object Loading : UiState()
        data class Success(val games: List<GameModel>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Init)
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        loadGames(forceRefresh = true)
    }

    fun loadGames(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = getPopularGames(forceRefresh)
            result.fold(
                onSuccess = { games ->
                    _state.value = UiState.Success(games)
                },
                onFailure = { exception ->
                    Log.e("GamesListViewModel", "Error al cargar juegos: ${exception.message}", exception)
                    _state.value = UiState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }
}

package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesLocalDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.repository.GamesRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlatformGamesViewModel (
    private val repository: GamesLocalDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Init)
    val state: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Init: UiState()
        object Loading : UiState()
        data class Success(val games: List<GameModel>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadGamesByPlatform(platform: String, limit: Int = 50) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val games = repository.getGamesByPlatform(platform, limit)
                _uiState.value = UiState.Success(games)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar")
            }
        }
    }
}
package com.ucb.proyectofinalgamerteca.features.games.presentation.filters

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class DeveloperGamesViewModel(
    private val repository: IGamesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Init)
    val state: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Init : UiState()
        object Loading : UiState()
        data class Success(val games: List<GameModel>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadGamesByDeveloper(developer: String, limit: Int = 50) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val games = repository.getGamesByDeveloper(developer, limit)
                _uiState.value = UiState.Success(games)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("DeveloperGamesViewModel", "Error al cargar juegos por desarrollador", e)
                _uiState.value = UiState.Error(e.message ?: "Error al cargar")
            }
        }
    }
}
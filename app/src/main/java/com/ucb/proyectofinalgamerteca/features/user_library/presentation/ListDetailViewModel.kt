package com.ucb.proyectofinalgamerteca.features.user_library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListDetailViewModel(
    private val libraryRepo: IUserLibraryRepository,
    private val gamesRepo: IGamesRepository
) : ViewModel() {

    data class UiState(
        val listInfo: CustomGameList? = null,
        val games: List<GameModel> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun loadList(listId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            libraryRepo.getListById(listId).fold(
                onSuccess = { list ->
                    if (list != null) {
                        loadGamesFromList(list)
                    } else {
                        _uiState.value = UiState(isLoading = false, error = "Lista no encontrada")
                    }
                },
                onFailure = { exception ->
                    android.util.Log.e("FIREBASE_ERROR", "Error cargando lista: ${exception.message}", exception)

                    _uiState.value = UiState(isLoading = false, error = "Error de conexión: ${exception.message}")
                }
            )
        }
    }

    private suspend fun loadGamesFromList(list: CustomGameList) {
        _uiState.value = UiState(listInfo = list, isLoading = true)

        val loadedGames = mutableListOf<GameModel>()

        list.gameIds.forEach { gameId ->
            gamesRepo.getGameById(gameId).onSuccess { game ->
                loadedGames.add(game)
            }
        }

        _uiState.value = UiState(
            listInfo = list,
            games = loadedGames,
            isLoading = false
        )
    }
}
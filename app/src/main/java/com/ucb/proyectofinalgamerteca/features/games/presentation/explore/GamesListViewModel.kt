package com.ucb.proyectofinalgamerteca.features.games.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.SearchType
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.SearchGamesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class GamesListViewModel(
    private val searchGames: SearchGamesUseCase
) : ViewModel() {

    data class UiState(
        val games: List<GameModel> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    // Estados de Filtros
    private var currentQuery = ""
    private var currentOffset = 0

    var isDateFilterActive = false
        private set

    var currentSearchType = SearchType.NAME
        private set

    init {
        loadGames(reset = true)
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery = query
        loadGames(reset = true)
    }

    fun onSearchTypeChanged(type: SearchType) {
        currentSearchType = type
        loadGames(reset = true)
    }

    fun onDateFilterToggle() {
        isDateFilterActive = !isDateFilterActive
        loadGames(reset = true)
    }

    fun onLoadMore() {
        loadGames(reset = false)
    }

    private fun loadGames(reset: Boolean) {
        if (reset) {
            currentOffset = 0
            _uiState.value = _uiState.value.copy(isLoading = true, games = emptyList())
        } else {
            if (_uiState.value.isLoading) return
            _uiState.value = _uiState.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            val (startDate, endDate) = if (isDateFilterActive) {
                val calendar = Calendar.getInstance()
                val end = calendar.timeInMillis / 1000

                calendar.add(Calendar.YEAR, -1)

                val start = calendar.timeInMillis / 1000
                Pair(start, end)
            } else {
                Pair(null, null)
            }

            // Llamada al UseCase con el SearchType
            searchGames(currentQuery, currentSearchType, startDate, endDate, currentOffset).fold(
                onSuccess = { newGames ->
                    val currentList = if (reset) emptyList() else _uiState.value.games
                    _uiState.value = _uiState.value.copy(
                        games = currentList + newGames,
                        isLoading = false,
                        error = null
                    )
                    currentOffset += newGames.size
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
            )
        }
    }
}

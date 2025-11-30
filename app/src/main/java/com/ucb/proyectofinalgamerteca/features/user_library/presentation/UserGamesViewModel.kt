package com.ucb.proyectofinalgamerteca.features.user_library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.UserGame
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserGamesViewModel(
    private val libraryRepository: IUserLibraryRepository,
    private val gamesRepository: IGamesRepository,
    private val authRepo: FirebaseRepository
) : ViewModel() {

    data class GameSection(
        val title: String,
        val games: List<UserGame>
    )

    private val _sections = MutableStateFlow<List<GameSection>>(emptyList())
    val sections: StateFlow<List<GameSection>> = _sections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadGames(filter: String) {
        val uid = authRepo.getCurrentUserId() ?: return
        _isLoading.value = true

        viewModelScope.launch {
            val result = when (filter) {
                "favorites" -> libraryRepository.getFavorites(uid)
                "reviews" -> libraryRepository.getRatedGames(uid)
                else -> libraryRepository.getGamesWithAnyStatus(uid)
            }

            result.onSuccess { rawGames ->
                // Hidratar con portadas de Room
                val gamesWithImages = rawGames.map { userGame ->
                    val localGame = gamesRepository.getGameById(userGame.gameId)
                    val cover = localGame.getOrNull()?.coverUrl
                    userGame.copy(coverUrl = cover)
                }

                if (filter == "favorites") {
                    _sections.value = listOf(GameSection("", gamesWithImages))
                } else if (filter == "reviews") {
                    _sections.value = listOf(GameSection("", gamesWithImages))
                } else {
                    // Para "Mis Juegos", sí usamos títulos de categoría
                    val grouped = gamesWithImages.groupBy { it.status }
                    val newSections = mutableListOf<GameSection>()
                    val categories = listOf(
                        GameStatus.PLAYING to "Jugando Actualmente",
                        GameStatus.PLAYED to "Juegos Terminados",
                        GameStatus.WISHLIST to "Lista de Deseos",
                        GameStatus.OWNED to "En Biblioteca",
                    )
                    categories.forEach { (status, title) ->
                        val gamesInStatus = grouped[status] ?: emptyList()
                        if (gamesInStatus.isNotEmpty()) {
                            newSections.add(GameSection(title, gamesInStatus))
                        }
                    }
                    _sections.value = newSections
                }
            }
            _isLoading.value = false
        }
    }
}
package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetGameDetailsUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.AddGameToLibraryUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.AddGameToListUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.CreateCustomListUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.GetUserGameInteractionUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.GetUserListsUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.SetUserRatingUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val getGameDetails: GetGameDetailsUseCase,
    private val addGameToLibrary: AddGameToLibraryUseCase,
    private val getUserInteraction: GetUserGameInteractionUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val setUserRating: SetUserRatingUseCase,
    private val getUserLists: GetUserListsUseCase,
    private val createCustomList: CreateCustomListUseCase,
    private val addGameToList: AddGameToListUseCase,
    private val repo: FirebaseRepository
) : ViewModel() {

    sealed class UiState {
        object Init : UiState()
        object Loading : UiState()
        data class Success(val game: GameModel) : UiState()
        data class Error(val message: String) : UiState()
    }

    data class UserInteractionState(
        val isFavorite: Boolean = false,
        val userRating: Int = 0,
        val status: GameStatus? = null,
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow<UiState>(UiState.Init)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _userState = MutableStateFlow(UserInteractionState())
    val userState: StateFlow<UserInteractionState> = _userState.asStateFlow()

    private val _relatedGames = MutableStateFlow<List<GameModel>>(emptyList())
    val relatedGames: StateFlow<List<GameModel>> = _relatedGames.asStateFlow()

    // --- ESTADO PARA LISTAS PERSONALIZADAS ---
    private val _userLists = MutableStateFlow<List<CustomGameList>>(emptyList())
    val userLists: StateFlow<List<CustomGameList>> = _userLists.asStateFlow()

    private val _showListDialog = MutableStateFlow(false)
    val showListDialog: StateFlow<Boolean> = _showListDialog.asStateFlow()

    private var currentGame: GameModel? = null

    fun loadGameDetails(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = UiState.Loading

            // 1. Cargar detalle API
            val result = getGameDetails(gameId)
            result.fold(
                onSuccess = { game ->
                    currentGame = game
                    _state.value = UiState.Success(game)

                    // 2. Cargar interacción de usuario (usando el ID del repo)
                    repo.getCurrentUserId()?.let { uid ->
                        loadUserInteraction(uid, gameId)
                    }
                },
                onFailure = { _state.value = UiState.Error(it.message ?: "Error") }
            )
        }
    }

    private suspend fun loadUserInteraction(uid: String, gameId: Long) {
        getUserInteraction(uid, gameId).onSuccess { userGame ->
            _userState.value = _userState.value.copy(
                isFavorite = userGame?.isFavorite ?: false,
                userRating = userGame?.userRating ?: 0,
                status = userGame?.status
            )
        }
    }

    // --- Acciones de UI ---

    fun onStatusSelected(status: GameStatus) {
        val game = currentGame ?: return
        val uid = repo.getCurrentUserId() ?: return

        val newStatus = if (_userState.value.status == status) null else status
        _userState.value = _userState.value.copy(status = newStatus)

        viewModelScope.launch(Dispatchers.IO) {
            addGameToLibrary(uid, game, newStatus)
        }
    }

    fun onToggleFavorite() {
        val game = currentGame ?: return
        val uid = repo.getCurrentUserId() ?: return

        val newFav = !_userState.value.isFavorite
        _userState.value = _userState.value.copy(isFavorite = newFav)

        viewModelScope.launch(Dispatchers.IO) {
            // Aseguramos que el juego exista en la librería al marcar fav
            addGameToLibrary(uid, game, _userState.value.status)
            toggleFavorite(uid, game.id, newFav)
        }
    }

    fun onRatingChanged(rating: Int) {
        val game = currentGame ?: return
        val uid = repo.getCurrentUserId() ?: return

        val newRating = if (_userState.value.userRating == rating) 0 else rating

        _userState.value = _userState.value.copy(userRating = newRating)

        viewModelScope.launch(Dispatchers.IO) {
            addGameToLibrary(uid, game, _userState.value.status)
            setUserRating(uid, game.id, newRating)
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

    // --- FUNCIONES DE LISTAS ---

    fun openListDialog() {
        val uid = repo.getCurrentUserId() ?: return
        _showListDialog.value = true
        // Cargamos las listas al abrir el diálogo
        viewModelScope.launch(Dispatchers.IO) {
            getUserLists(uid).onSuccess { result ->
                _userLists.value = result.first
            }
        }
    }

    fun closeListDialog() {
        _showListDialog.value = false
    }

    fun onCreateList(listName: String, description: String, isPublic: Boolean) {
        val uid = repo.getCurrentUserId() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = repo.getUserProfile(uid).getOrNull()
            val userName = userProfile?.get("username") as? String ?: "Anónimo"

            val newList = CustomGameList(
                name = listName,
                description = description,
                isPublic = isPublic,
                ownerName = userName
            )

            createCustomList(uid, newList).onSuccess {
                getUserLists(uid).onSuccess { result ->
                    _userLists.value = result.first
                }
            }
        }
    }

    fun addGameToCustomList(list: CustomGameList) {
        val game = currentGame ?: return
        val uid = repo.getCurrentUserId() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            addGameToList(uid, list.listId, game.id).onSuccess {
                // Opcional: Mostrar mensaje de éxito o cerrar diálogo
                _showListDialog.value = false
            }
        }
    }
}
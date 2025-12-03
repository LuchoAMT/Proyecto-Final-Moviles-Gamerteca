package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.CreateCustomListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicListsViewModel(
    private val libraryRepo: IUserLibraryRepository,
    private val gamesRepo: IGamesRepository,
    private val authRepo: FirebaseRepository,
    private val createCustomListUseCase: CreateCustomListUseCase
) : ViewModel() {

    data class ListWithCovers(val list: CustomGameList, val coverUrls: List<String>)

    private val _uiState = MutableStateFlow<List<ListWithCovers>>(emptyList())
    val uiState = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow() // Carga inicial

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow() // Carga de paginación

    private var lastVisibleDocument: Any? = null
    private var isEndOfList = false

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private var allLoadedListsBackup: List<ListWithCovers> = emptyList() // Para filtrar localmente

    init {
        loadPublicLists(initialLoad = true)
    }

    fun loadPublicLists(initialLoad: Boolean = false) {
        if (isEndOfList && !initialLoad) return
        if (_isLoading.value || _isLoadingMore.value) return

        if (initialLoad) {
            _isLoading.value = true
            lastVisibleDocument = null
            isEndOfList = false
            _uiState.value = emptyList()
        } else {
            _isLoadingMore.value = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            libraryRepo.getAllPublicLists(lastVisible = lastVisibleDocument, limit = 20)
                .onSuccess { (newLists, lastDoc) ->

                    val processedNewLists = newLists.map { list ->
                        val covers = list.gameIds.take(3).mapNotNull { id ->
                            gamesRepo.getGameById(id).getOrNull()?.coverUrl
                        }
                        ListWithCovers(list, covers)
                    }

                    if (newLists.size < 20) {
                        isEndOfList = true
                    }
                    lastVisibleDocument = lastDoc

                    val currentList = if (initialLoad) emptyList() else _uiState.value
                    val updatedList = currentList + processedNewLists

                    _uiState.value = updatedList
                    allLoadedListsBackup = updatedList
                }

            _isLoading.value = false
            _isLoadingMore.value = false
        }
    }

    fun createNewList(name: String, description: String, isPublic: Boolean) {
        val uid = authRepo.getCurrentUserId() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            // Obtener nombre de usuario para guardarlo
            val userProfile = authRepo.getUserProfile(uid).getOrNull()
            val userName = userProfile?.get("username") as? String ?: "Anónimo"

            val newList = CustomGameList(
                name = name,
                description = description,
                isPublic = isPublic,
                ownerName = userName
            )

            createCustomListUseCase(uid, newList).onSuccess {
                // Si la lista es pública, recargamos para que aparezca
                if (isPublic) {
                    loadPublicLists()
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _uiState.value = allLoadedListsBackup
        } else {
            _uiState.value = allLoadedListsBackup.filter {
                it.list.name.contains(query, ignoreCase = true) ||
                        it.list.ownerName.contains(query, ignoreCase = true)
            }
        }
    }
}
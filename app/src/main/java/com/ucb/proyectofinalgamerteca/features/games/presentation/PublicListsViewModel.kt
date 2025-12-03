package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicListsViewModel(
    private val libraryRepo: IUserLibraryRepository,
    private val gamesRepo: IGamesRepository
) : ViewModel() {

    data class ListWithCovers(val list: CustomGameList, val coverUrls: List<String>)

    private var allLists: List<ListWithCovers> = emptyList()
    private val _uiState = MutableStateFlow<List<ListWithCovers>>(emptyList())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init { loadPublicLists() }

    fun loadPublicLists() {
        viewModelScope.launch {
            libraryRepo.getAllPublicLists().onSuccess { lists ->
                val processed = lists.map { list ->
                    val covers = list.gameIds.take(3).mapNotNull { id ->
                        gamesRepo.getGameById(id).getOrNull()?.coverUrl
                    }
                    ListWithCovers(list, covers)
                }
                allLists = processed
                _uiState.value = processed
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _uiState.value = allLists
        } else {
            _uiState.value = allLists.filter {
                it.list.name.contains(query, ignoreCase = true) ||
                        it.list.ownerName.contains(query, ignoreCase = true)
            }
        }
    }
}
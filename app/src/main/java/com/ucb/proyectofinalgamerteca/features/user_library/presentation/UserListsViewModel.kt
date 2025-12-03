package com.ucb.proyectofinalgamerteca.features.user_library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.GetUserListsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserListsViewModel(
    private val getUserLists: GetUserListsUseCase,
    private val gamesRepo: IGamesRepository,
    private val authRepo: FirebaseRepository
) : ViewModel() {

    data class ListWithCovers(val list: CustomGameList, val coverUrls: List<String>)

    private val _uiState = MutableStateFlow<List<ListWithCovers>>(emptyList())
    val uiState = _uiState.asStateFlow()

    init {
        loadMyLists()
    }

    fun loadMyLists() {
        val uid = authRepo.getCurrentUserId() ?: return
        viewModelScope.launch {
            getUserLists(uid).onSuccess { result ->
                val myLists = result.first
                val processed = myLists.map { list ->
                    val covers = list.gameIds.take(3).mapNotNull { id ->
                        gamesRepo.getGameById(id).getOrNull()?.coverUrl
                    }
                    ListWithCovers(list, covers)
                }
                _uiState.value = processed
            }
        }
    }
}
package com.ucb.proyectofinalgamerteca.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: FirebaseRepository
) : ViewModel() {

    // Estado de la UI
    data class UiState(
        val userName: String = "Cargando...",
        val email: String = "",
        val profileImageUrl: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val uid = repo.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                val result = repo.getUserProfile(uid)

                result.onSuccess { data ->
                    _uiState.value = UiState(
                        userName = data["username"] as? String ?: "Gamer",
                        email = data["email"] as? String ?: "",
                        profileImageUrl = null
                    )
                }
            }
        }
    }
}
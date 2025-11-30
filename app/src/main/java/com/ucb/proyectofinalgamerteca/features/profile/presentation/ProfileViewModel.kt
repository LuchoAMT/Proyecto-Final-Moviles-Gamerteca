package com.ucb.proyectofinalgamerteca.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: FirebaseRepository
) : ViewModel() {

    data class UiState(
        val username: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = "",
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = repo.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                val result = repo.getUserProfile(uid)
                result.onSuccess { data ->
                    _uiState.value = UiState(
                        username = data["username"] as? String ?: "Sin nombre",
                        email = data["email"] as? String ?: "",
                        phone = data["phone"] as? String ?: "",
                        password = "",
                        isLoading = false
                    )
                }
            }
        }
    }
}
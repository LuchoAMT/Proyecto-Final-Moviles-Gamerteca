package com.ucb.proyectofinalgamerteca.features.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: FirebaseRepository = FirebaseRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(username = newValue)
    }

    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(confirmPasssword = newValue)
    }

    fun register() {
        val current = _uiState.value

        // Validaciones básicas
        if (current.username.isBlank() || current.email.isBlank() ||
            current.password.isBlank() || current.confirmPasssword.isBlank()
        ) {
            _uiState.value = current.copy(errorMessage = "Por favor completa todos los campos")
            return
        }

        if (current.password != current.confirmPasssword) {
            _uiState.value = current.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repo.registerUser(_uiState.value.email, _uiState.value.password, _uiState.value.username)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(success = true, isLoading = false)
            } else {
                _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message,
                    isLoading = false
                )
            }
        }
    }
}
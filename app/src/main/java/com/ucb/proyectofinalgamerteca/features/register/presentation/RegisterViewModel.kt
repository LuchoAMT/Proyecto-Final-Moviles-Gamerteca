package com.ucb.proyectofinalgamerteca.features.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(nickname = newValue)
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
        if (current.nickname.isBlank() || current.email.isBlank() ||
            current.password.isBlank() || current.confirmPasssword.isBlank()
        ) {
            _uiState.value = current.copy(errorMessage = "Por favor completa todos los campos")
            return
        }

        if (current.password != current.confirmPasssword) {
            _uiState.value = current.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        // Simular proceso de registro
        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, errorMessage = null)
            delay(1500) // simula llamada a backend o Firebase

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                success = true
            )
        }
    }
}
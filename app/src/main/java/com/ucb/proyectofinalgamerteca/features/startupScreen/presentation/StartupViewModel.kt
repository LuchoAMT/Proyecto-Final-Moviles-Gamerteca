package com.ucb.proyectofinalgamerteca.features.startupScreen.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StartupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StartupUiState())
    val uiState: StateFlow<StartupUiState> = _uiState

    fun onContinueClicked() {
        // aquí puedes notificar que se debe navegar a login/registro
    }
}
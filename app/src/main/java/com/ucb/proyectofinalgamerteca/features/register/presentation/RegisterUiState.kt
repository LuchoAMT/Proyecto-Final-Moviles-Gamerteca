package com.ucb.proyectofinalgamerteca.features.register.presentation

data class RegisterUiState(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPasssword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
) {
}
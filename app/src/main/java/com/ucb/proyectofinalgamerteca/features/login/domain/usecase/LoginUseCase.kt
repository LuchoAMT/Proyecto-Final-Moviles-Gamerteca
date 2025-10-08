package com.ucb.proyectofinalgamerteca.features.login.domain.usecase

import com.ucb.proyectofinalgamerteca.features.login.domain.model.User

class LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return if (email == "test@gmail.com" && password == "123456") {
            Result.success(User(email = email, name = "Usuario de prueba"))
        } else {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }
}
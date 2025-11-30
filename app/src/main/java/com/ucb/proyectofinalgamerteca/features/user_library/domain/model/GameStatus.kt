package com.ucb.proyectofinalgamerteca.features.user_library.domain.model

enum class GameStatus {
    PLAYED,     // Jugado
    PLAYING,    // Jugando
    OWNED,      // Biblioteca / Lo tengo
    WISHLIST    // Lo quiero
}

fun GameStatus.toDisplayName(): String {
    return when (this) {
        GameStatus.PLAYED -> "Jugado"
        GameStatus.PLAYING -> "Jugando"
        GameStatus.WISHLIST -> "Lo Quiero"
        GameStatus.OWNED -> "Lo tengo"
    }
}
package com.ucb.proyectofinalgamerteca.features.user_library.domain.model

data class UserGame(
    val gameId: Long,
    val name: String,
    val coverUrl: String?,

    // Estados e Interacciones
    val status: GameStatus? = null,      // PLAYED, PLAYING, etc.
    val isFavorite: Boolean = false,
    val hasReview: Boolean = false,
    val userRating: Int? = null,         // Tu calificación (1-5)

    // Listas
    val customLists: List<String> = emptyList(),

    // Metadata
    val addedAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
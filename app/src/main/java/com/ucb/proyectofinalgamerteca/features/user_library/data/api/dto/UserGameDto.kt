package com.ucb.proyectofinalgamerteca.features.user_library.data.api.dto

import com.google.firebase.firestore.PropertyName
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.UserGame

data class UserGameDto(
    @get:PropertyName("name") var name: String = "",
    @get:PropertyName("status") var status: String? = null,
    @get:PropertyName("isFavorite") var isFavorite: Boolean = false,
    @get:PropertyName("userRating") var userRating: Int = 0,
    @get:PropertyName("lastUpdated") var lastUpdated: Long = System.currentTimeMillis()
) {
    fun toModel(documentId: Long): UserGame {
        return UserGame(
            gameId = documentId,
            name = name,
            coverUrl = null,
            status = status?.let { try { GameStatus.valueOf(it) } catch (e: Exception) { null } },
            isFavorite = isFavorite,
            userRating = userRating,
            lastUpdated = lastUpdated
        )
    }
}
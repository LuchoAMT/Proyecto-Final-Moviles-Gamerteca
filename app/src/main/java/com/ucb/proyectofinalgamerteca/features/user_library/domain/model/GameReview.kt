package com.ucb.proyectofinalgamerteca.features.user_library.domain.model

data class GameReview(
    val reviewId: String = "",
    val gameId: Long,
    val gameName: String,
    val gameCoverUrl: String?,
    val userId: String,
    val userNickname: String,
    val rating: Int,
    val title: String,
    val content: String,
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val isEdited: Boolean = false
)
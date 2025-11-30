package com.ucb.proyectofinalgamerteca.features.user_library.domain.model

data class CustomGameList(
    val listId: String = "",
    val name: String,
    val description: String = "",
    val gameIds: List<Long> = emptyList(),
    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

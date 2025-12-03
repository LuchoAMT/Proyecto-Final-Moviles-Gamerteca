package com.ucb.proyectofinalgamerteca.features.user_library.domain.model

import com.google.firebase.firestore.PropertyName

data class CustomGameList(
    @PropertyName("listId") val listId: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("gameIds") val gameIds: List<Long> = emptyList(),
    @PropertyName("public") val isPublic: Boolean = false,
    @PropertyName("ownerId") val ownerId: String = "",
    @PropertyName("ownerName") val ownerName: String = "",
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updatedAt") val updatedAt: Long = System.currentTimeMillis()
)

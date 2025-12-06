package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName

data class FranchiseSearchDto(
    val id: Long,
    val name: String?,
    @SerializedName("games") val games: List<Long>? = null
)
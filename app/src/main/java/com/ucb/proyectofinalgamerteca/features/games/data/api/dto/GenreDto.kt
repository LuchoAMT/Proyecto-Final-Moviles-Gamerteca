package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName

data class GenreDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)
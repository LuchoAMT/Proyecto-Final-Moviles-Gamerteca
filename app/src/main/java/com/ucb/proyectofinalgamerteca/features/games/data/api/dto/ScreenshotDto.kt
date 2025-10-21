package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName

data class ScreenshotDto(
    @SerializedName("id") val id: Long,
    @SerializedName("url") val url: String?
)
package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName

data class GameDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("first_release_date") val firstReleaseDate: Long?,
    @SerializedName("platforms") val platforms: List<PlatformDto>?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("cover") val cover: CoverDto?
)
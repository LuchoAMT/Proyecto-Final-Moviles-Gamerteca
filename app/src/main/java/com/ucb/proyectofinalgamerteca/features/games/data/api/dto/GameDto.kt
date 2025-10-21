package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import kotlin.collections.filter

data class GameDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("first_release_date") val firstReleaseDate: Long?,
    @SerializedName("platforms") val platforms: List<PlatformDto>?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("cover") val cover: CoverDto?,
    @SerializedName("involved_companies") val involvedCompanies: List<InvolvedCompanyDto>?,
    @SerializedName("screenshots") val screenshots: List<ScreenshotDto>?,
    @SerializedName("similar_games") val similarGames: List<Long>? = emptyList()
) {
    fun toModel(): GameModel {

        return GameModel(
            id = id,
            name = name ?: "Sin título",
            summary = summary,
            rating = rating,
            releaseDate = firstReleaseDate,
            coverUrl = cover?.url?.let { url ->
                if (url.startsWith("//")) "https:$url" else url
            },
            platforms = platforms?.map { it.name } ?: emptyList(),
            genres = genres?.map { it.name } ?: emptyList(),
            similarGames = similarGames ?: emptyList(),
            involvedCompanies = involvedCompanies
                ?.filter { it.developer }
                ?.mapNotNull { it.company?.name }
                ?: emptyList(),
            screenshots = screenshots?.mapNotNull { it.url?.let { url ->
                if (url.startsWith("//")) "https:$url" else url
            } } ?: emptyList()
        )
    }
}

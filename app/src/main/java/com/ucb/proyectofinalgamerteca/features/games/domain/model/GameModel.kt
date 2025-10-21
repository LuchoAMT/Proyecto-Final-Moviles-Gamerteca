package com.ucb.proyectofinalgamerteca.features.games.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GameModel(
    val id: Long,
    val name: String,
    val summary: String?,
    val rating: Double?,
    val releaseDate: Long?,
    val coverUrl: String?,
    val platforms: List<String>,
    val genres: List<String>,
    val similarGames: List<Long> = emptyList(),
    val involvedCompanies: List<String> = emptyList(),
    val screenshots: List<String> = emptyList()
) {
    fun getFormattedReleaseDate(): String {
        return releaseDate?.let {
            val date = Date(it * 1000)
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        } ?: "Fecha desconocida"
    }

    fun getRatingStars(): Int {
        return rating?.let { (it / 20).toInt() } ?: 0
    }

    fun getHighResolutionCoverUrl(): String? {
        return coverUrl?.replace("t_thumb", "t_cover_big")
    }

    fun getRatingText(): String {
        return rating?.let {
            val ratingValue = it / 20
            val roundedValue = "%.1f".format(ratingValue)
            "$roundedValue/5"
        } ?: "Sin calificación"
    }
}
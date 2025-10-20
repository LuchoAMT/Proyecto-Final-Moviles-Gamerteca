package com.ucb.proyectofinalgamerteca.features.games.domain.model

import com.ucb.proyectofinalgamerteca.features.games.data.database.entity.GameEntity
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
    val genres: List<String>
) {
    fun getFormattedReleaseDate(): String {
        return releaseDate?.let {
            val date = Date(it * 1000)
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        } ?: "Fecha desconocida"
    }

    fun getRatingStars(): Int = rating?.let { (it / 20).toInt() } ?: 0

    fun getHighResolutionCoverUrl(): String? =
        coverUrl?.replace("t_thumb", "t_cover_big")

    fun toEntity(): GameEntity {
        return GameEntity(
            id = id,
            name = name,
            summary = summary,
            rating = rating,
            releaseDate = releaseDate,
            coverUrl = coverUrl,
            platforms = platforms.joinToString(","),
            genres = genres.joinToString(",")
        )
    }
}
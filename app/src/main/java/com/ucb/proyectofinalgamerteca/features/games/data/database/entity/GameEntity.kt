package com.ucb.proyectofinalgamerteca.features.games.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "summary") val summary: String?,
    @ColumnInfo(name = "rating") val rating: Double?,
    @ColumnInfo(name = "release_date") val releaseDate: Long?,
    @ColumnInfo(name = "cover_url") val coverUrl: String?,
    @ColumnInfo(name = "platforms") val platforms: String?, // Guardado como String separado por comas
    @ColumnInfo(name = "genres") val genres: String?, // Guardado como String separado por comas
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    fun toModel(): GameModel {
        return GameModel(
            id = id,
            name = name,
            summary = summary,
            rating = rating,
            releaseDate = releaseDate,
            coverUrl = coverUrl,
            platforms = platforms?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            genres = genres?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }
}

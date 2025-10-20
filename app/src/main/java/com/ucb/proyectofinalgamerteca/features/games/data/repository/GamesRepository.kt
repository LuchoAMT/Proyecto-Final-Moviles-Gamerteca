package com.ucb.proyectofinalgamerteca.features.games.data.repository

import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.GameDto
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesLocalDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesRemoteDataSource
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository


private const val IGDB_IMAGE_BASE = "https:"

class GamesRepository(
    private val remote: GamesRemoteDataSource,
    private val local: GamesLocalDataSource
) : IGamesRepository {

    companion object {
        private const val IGDB_IMAGE_BASE = "https:"
        private const val TAG = "GamesRepository"
    }

    override suspend fun getPopularGames(forceRefresh: Boolean): Result<List<GameModel>> {
        return remote.getPopularGames().fold(
            onSuccess = { dtos ->
                val models = dtos.map { dto -> dto.toModel() }
                local.insertGames(models)
                Log.d(TAG, "✅ Insertados ${models.size} juegos en la BD local")
                Result.success(models)
            },
            onFailure = { exception ->
                val localGames = local.getAllGames()
                if (localGames.isNotEmpty()) {
                    Log.w(TAG, "⚠️ Falló API, usando ${localGames.size} juegos locales")
                    Result.success(localGames)
                } else {
                    Log.e(TAG, "❌ Error al obtener juegos: ${exception.message}", exception)
                    Result.failure(exception)
                }
            }
        )
    }

    override suspend fun getGameById(gameId: Long): Result<GameModel> {
        val localGame = local.getGameById(gameId)
        if (localGame != null) {
            Log.d(TAG, "🎮 Juego obtenido localmente: ${localGame.name}")
            return Result.success(localGame)
        }

        return try {
            val remoteResult = remote.getGameById(gameId)
            remoteResult.fold(
                onSuccess = { dto ->
                    val model = dto.toModel()
                    local.insertGame(model)
                    Log.d(TAG, "🌐 Juego descargado y guardado: ${model.name}")
                    Result.success(model)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ Error al obtener juego remoto: ${e.message}", e)
                    Result.failure(e)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción en getGameById: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Mapea GameDto → GameModel
     */
    private fun GameDto.toModel(): GameModel {
        return GameModel(
            id = id,
            name = name ?: "Sin título",
            summary = summary,
            rating = rating,
            releaseDate = firstReleaseDate,
            coverUrl = cover?.url?.let { url ->
                if (url.startsWith("//")) "$IGDB_IMAGE_BASE$url" else url
            },
            platforms = platforms?.mapNotNull { it.name } ?: emptyList(),
            genres = genres?.mapNotNull { it.name } ?: emptyList()
        )
    }
}


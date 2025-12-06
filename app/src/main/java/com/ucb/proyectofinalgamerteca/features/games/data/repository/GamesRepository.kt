package com.ucb.proyectofinalgamerteca.features.games.data.repository

import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.GameDto
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesLocalDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesRemoteDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.SearchType
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import kotlin.coroutines.cancellation.CancellationException

class GamesRepository(
    private val remote: GamesRemoteDataSource,
    private val local: GamesLocalDataSource
) : IGamesRepository {

    companion object {
        private const val IGDB_IMAGE_BASE = "https:"
        private const val TAG = "GamesRepository"
    }

    override suspend fun searchGames(
        query: String,
        searchType: SearchType,
        startDate: Long?,
        endDate: Long?,
        offset: Int
    ): Result<List<GameModel>> {
        return remote.searchGames(query, searchType, startDate, endDate, 20, offset).fold(
            onSuccess = { dtos ->
                val models = dtos.map { it.toModel() }
                if (models.isNotEmpty()) {
                    local.insertGames(models)
                }

                Result.success(models)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    override suspend fun getGameById(gameId: Long): Result<GameModel> {
        return try {
            val remoteResult = remote.getGameById(gameId)
            remoteResult.fold(
                onSuccess = { dto ->
                    val model = dto.toModel()
                    local.insertGame(model)
                    Result.success(model)
                },
                onFailure = { e ->
                    if (e is CancellationException) throw e
                    Log.e(TAG, "❌ Error API, buscando en local: ${e.message}")

                    // Fallback a Local si falla API
                    val localGame = local.getGameById(gameId)
                    if (localGame != null) {
                        Result.success(localGame)
                    } else {
                        Result.failure(e)
                    }
                }
            )
        } catch (e: Exception) {
            val localGame = local.getGameById(gameId)
            if (localGame != null) Result.success(localGame) else Result.failure(e)
        }
    }

    override suspend fun getGamesByReleaseYear(year: Int, limit: Int): List<GameModel> {
        val remoteDtos = remote.getGamesByReleaseYear(year, limit)
        val remoteModels = remoteDtos.map { it.toModel() }

        if (remoteModels.isNotEmpty()) {
            local.insertGames(remoteModels)
            return remoteModels
        }
        return local.getGamesByReleaseYear(year, limit)
    }

    override suspend fun getGamesByGenre(genre: String, limit: Int): List<GameModel> {
        val remoteDtos = remote.getGamesByGenre(genre, limit)
        val remoteModels = remoteDtos.map { it.toModel() }

        if (remoteModels.isNotEmpty()) {
            local.insertGames(remoteModels)
            return remoteModels
        }
        return local.getGamesByGenre(genre, limit)
    }

    override suspend fun getGamesByPlatform(platform: String, limit: Int): List<GameModel> {
        val remoteDtos = remote.getGamesByPlatform(platform, limit)
        val remoteModels = remoteDtos.map { it.toModel() }

        if (remoteModels.isNotEmpty()) {
            local.insertGames(remoteModels)
            return remoteModels
        }
        return local.getGamesByPlatform(platform, limit)
    }

    override suspend fun getGamesByDeveloper(developer: String, limit: Int): List<GameModel> {
        val remoteDtos = remote.getGamesByDeveloper(developer, limit)
        val remoteModels = remoteDtos.map { it.toModel() }

        if (remoteModels.isNotEmpty()) {
            local.insertGames(remoteModels)
            return remoteModels
        }
        return local.getGamesByDeveloper(developer, limit)
    }


    /**
     * Mapea GameDto → GameModel con TODOS los campos
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
            genres = genres?.mapNotNull { it.name } ?: emptyList(),
            similarGames = similarGames ?: emptyList(),
            involvedCompanies = involvedCompanies
                ?.mapNotNull { it.company?.name } ?: emptyList(), // Simplificado
            screenshots = screenshots?.mapNotNull { screenshot ->
                screenshot.url?.let { url ->
                    if (url.startsWith("//")) "$IGDB_IMAGE_BASE$url" else url
                }
            } ?: emptyList()
        )
    }
}


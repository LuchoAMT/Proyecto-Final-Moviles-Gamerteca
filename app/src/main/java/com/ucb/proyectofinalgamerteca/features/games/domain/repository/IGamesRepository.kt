package com.ucb.proyectofinalgamerteca.features.games.domain.repository

import com.ucb.proyectofinalgamerteca.features.games.data.datasource.SearchType
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel

interface IGamesRepository {
    suspend fun searchGames(
        query: String,
        searchType: SearchType,
        startDate: Long?,
        endDate: Long?,
        offset: Int
    ): Result<List<GameModel>>

    suspend fun getGameById(gameId: Long): Result<GameModel>
    suspend fun getGamesByReleaseYear(year: Int, limit: Int): List<GameModel>
    suspend fun getGamesByGenre(genre: String, limit: Int): List<GameModel>
    suspend fun getGamesByPlatform(platform: String, limit: Int): List<GameModel>
    suspend fun getGamesByDeveloper(developer: String, limit: Int): List<GameModel>
}
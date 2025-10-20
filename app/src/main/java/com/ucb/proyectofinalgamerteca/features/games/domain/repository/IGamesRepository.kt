package com.ucb.proyectofinalgamerteca.features.games.domain.repository

import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel

interface IGamesRepository {
    suspend fun getPopularGames(forceRefresh: Boolean = false): Result<List<GameModel>>
    suspend fun getGameById(gameId: Long): Result<GameModel>
}
package com.ucb.proyectofinalgamerteca.features.games.data.datasource

import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.data.database.dao.IGameDao
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel

class GamesLocalDataSource(
    private val dao: IGameDao
) {
    suspend fun getAllGames(): List<GameModel> {
        return dao.getAllGames().map { it.toModel() }
    }

    suspend fun getGameById(gameId: Long): GameModel? {
        return dao.getGameById(gameId)?.toModel()
    }

    suspend fun insertGames(games: List<GameModel>) {
        val entities = games.map { it.toEntity() }
        dao.insertGames(entities)
        Log.d("GamesLocalDataSource", "💾 Insertados ${entities.size} juegos")
    }

    suspend fun insertGame(game: GameModel) {
        dao.insertGame(game.toEntity())
        Log.d("GamesLocalDataSource", "💾 Insertado juego: ${game.name}")
    }
}
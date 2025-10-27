package com.ucb.proyectofinalgamerteca.features.games.data.datasource

import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.data.database.dao.IGameDao
import com.ucb.proyectofinalgamerteca.features.games.data.database.entity.GameEntity
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
        val entities = games.map { game ->
            GameEntity(
                id = game.id,
                name = game.name,
                summary = game.summary,
                rating = game.rating,
                releaseDate = game.releaseDate,
                coverUrl = game.coverUrl,
                platforms = game.platforms.joinToString(","),
                genres = game.genres.joinToString(","),
                involvedCompanies = game.involvedCompanies.joinToString(","),
                screenshots = game.screenshots.joinToString(","),
                similarGames = game.similarGames.joinToString(","),
                timestamp = System.currentTimeMillis()
            )
        }
        dao.insertGames(entities)
        Log.d("GamesLocalDataSource", "💾 Insertados ${entities.size} juegos")
    }

    suspend fun insertGame(game: GameModel) {
        dao.insertGame(
            GameEntity(
                id = game.id,
                name = game.name,
                summary = game.summary,
                rating = game.rating,
                releaseDate = game.releaseDate,
                coverUrl = game.coverUrl,
                platforms = game.platforms.joinToString(","),
                genres = game.genres.joinToString(","),
                involvedCompanies = game.involvedCompanies.joinToString(","),
                screenshots = game.screenshots.joinToString(","),
                similarGames = game.similarGames.joinToString(","),
                timestamp = System.currentTimeMillis()
            )
        )
        Log.d("GamesLocalDataSource", "💾 Insertado juego: ${game.name}")
    }

    suspend fun getRecentGames(limit: Int): List<GameModel> {
        return dao.getRecentGames(limit).map { it.toModel() }
    }

    suspend fun deleteAllGames() {
        dao.deleteAllGames()
    }

    suspend fun getGamesCount(): Int {
        return dao.getGamesCount()
    }

    suspend fun getGamesByPlatform(platform:String, limit: Int): List<GameModel> {
        return dao.getGamesByPlatform(platform = platform, limit = limit).map { it.toModel() }
    }
}
package com.ucb.proyectofinalgamerteca.features.games.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.proyectofinalgamerteca.features.games.data.database.entity.GameEntity

@Dao
interface IGameDao {
    @Query("SELECT * FROM games")
    suspend fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM games ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentGames(limit: Int): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    suspend fun getGameById(gameId: Long): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGamesCount(): Int
}
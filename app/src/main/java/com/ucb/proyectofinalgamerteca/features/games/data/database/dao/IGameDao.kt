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

    @Query("""
    SELECT * FROM games
    WHERE genres = :genre
       OR genres LIKE :genre || ',%'
       OR genres LIKE '%,' || :genre || ',%'
       OR genres LIKE '%,' || :genre
    ORDER BY timestamp DESC
    LIMIT :limit
    """)
    suspend fun getGamesByGenre(genre: String, limit: Int): List<GameEntity>

    @Query("""
    SELECT * FROM games 
    WHERE platforms = :platform
       OR platforms LIKE :platform || ',%' 
       OR platforms LIKE '%,' || :platform || ',%' 
       OR platforms LIKE '%,' || :platform
    ORDER BY timestamp DESC
    LIMIT :limit
    """)
    suspend fun getGamesByPlatform(platform: String, limit: Int): List<GameEntity>

    @Query("""
    SELECT * FROM games
    WHERE strftime('%Y', release_date / 1000, 'unixepoch') = :year
    ORDER BY timestamp DESC
    LIMIT :limit
""")
    suspend fun getGamesByReleaseYear(year: String, limit: Int): List<GameEntity>

    @Query("""
    SELECT * FROM games
    WHERE involved_companies LIKE '%' || :developer || '%'
    ORDER BY timestamp DESC
    LIMIT :limit
""")
    suspend fun getGamesByDeveloper(developer: String, limit: Int): List<GameEntity>

    @Query("SELECT id FROM games")
    suspend fun getAllGameIds(): List<Long>

}
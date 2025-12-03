package com.ucb.proyectofinalgamerteca.features.user_library.domain.repository

import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameReview
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.UserGame

interface IUserLibraryRepository {

    // =============================================================================================
    // 1. GESTIÓN DE BIBLIOTECA (Library & Status)
    // =============================================================================================

    suspend fun addGameToLibrary(userId: String, game: GameModel, status: GameStatus?): Result<Unit>

    suspend fun updateGameStatus(userId: String, gameId: Long, status: GameStatus): Result<Unit>

    suspend fun removeGameFromLibrary(userId: String, gameId: Long): Result<Unit>

    suspend fun getGameFromLibrary(userId: String, gameId: Long): Result<UserGame?>

    suspend fun getUserGames(userId: String, status: GameStatus? = null): Result<List<UserGame>>

    suspend fun getGamesWithAnyStatus(userId: String): Result<List<UserGame>>

    // =============================================================================================
    // 2. FAVORITOS Y RATING (Interaction)
    // =============================================================================================

    suspend fun toggleFavorite(userId: String, gameId: Long, isFavorite: Boolean): Result<Unit>

    suspend fun getFavorites(userId: String): Result<List<UserGame>>

    suspend fun setUserRating(userId: String, gameId: Long, rating: Int): Result<Unit>

    suspend fun getRatedGames(userId: String): Result<List<UserGame>>

    // =============================================================================================
    // 3. RESEÑAS (Reviews)
    // =============================================================================================

    suspend fun addReview(userId: String, review: GameReview): Result<String>
    suspend fun updateReview(userId: String, reviewId: String, review: GameReview): Result<Unit>
    suspend fun deleteReview(userId: String, reviewId: String): Result<Unit>
    suspend fun getUserReviews(userId: String): Result<List<GameReview>>
    suspend fun getGameReview(userId: String, gameId: Long): Result<GameReview?>

    // =============================================================================================
    // 4. LISTAS PERSONALIZADAS (Custom Lists)
    // =============================================================================================

    suspend fun createCustomList(userId: String, list: CustomGameList): Result<String>
    suspend fun getAllPublicLists(lastVisible: Any? = null, limit: Int = 20): Result<Pair<List<CustomGameList>, Any?>>
    suspend fun updateCustomList(userId: String, listId: String, list: CustomGameList): Result<Unit>
    suspend fun deleteCustomList(userId: String, listId: String): Result<Unit>
    suspend fun getUserLists(userId: String, lastVisible: Any? = null, limit: Int = 20): Result<Pair<List<CustomGameList>, Any?>>
    suspend fun addGameToList(userId: String, listId: String, gameId: Long): Result<Unit>
    suspend fun getListById(listId: String): Result<CustomGameList?>
    suspend fun removeGameFromList(userId: String, listId: String, gameId: Long): Result<Unit>
}
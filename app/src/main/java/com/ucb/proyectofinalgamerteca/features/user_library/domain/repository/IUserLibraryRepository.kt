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

    /** Guarda un juego con su estado (Jugando, etc). NO guarda rating ni favoritos aquí. */
    suspend fun addGameToLibrary(userId: String, game: GameModel, status: GameStatus?): Result<Unit>

    /** Actualiza solo el estado de un juego existente. */
    suspend fun updateGameStatus(userId: String, gameId: Long, status: GameStatus): Result<Unit>

    /** Elimina el juego de la biblioteca. */
    suspend fun removeGameFromLibrary(userId: String, gameId: Long): Result<Unit>

    /** Obtiene un juego específico con todos sus datos de usuario. */
    suspend fun getGameFromLibrary(userId: String, gameId: Long): Result<UserGame?>

    /** Obtiene juegos filtrados por un estado específico (ej: Solo "Jugando"). */
    suspend fun getUserGames(userId: String, status: GameStatus? = null): Result<List<UserGame>>

    /** Obtiene TODOS los juegos que tienen algún estado asignado (Para la sección "Mis Juegos"). */
    suspend fun getGamesWithAnyStatus(userId: String): Result<List<UserGame>>

    // =============================================================================================
    // 2. FAVORITOS Y RATING (Interaction)
    // =============================================================================================

    /** Marca o desmarca como favorito. */
    suspend fun toggleFavorite(userId: String, gameId: Long, isFavorite: Boolean): Result<Unit>

    /** Obtiene solo la lista de favoritos. */
    suspend fun getFavorites(userId: String): Result<List<UserGame>>

    /** Guarda la calificación del usuario (1-5 estrellas). */
    suspend fun setUserRating(userId: String, gameId: Long, rating: Int): Result<Unit>

    /** Obtiene los juegos que el usuario ha calificado (Rating > 0). */
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
    suspend fun updateCustomList(userId: String, listId: String, list: CustomGameList): Result<Unit>
    suspend fun deleteCustomList(userId: String, listId: String): Result<Unit>
    suspend fun getUserLists(userId: String): Result<List<CustomGameList>>
    suspend fun addGameToList(userId: String, listId: String, gameId: Long): Result<Unit>
    suspend fun removeGameFromList(userId: String, listId: String, gameId: Long): Result<Unit>
}
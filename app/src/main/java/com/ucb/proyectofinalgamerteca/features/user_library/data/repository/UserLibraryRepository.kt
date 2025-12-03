package com.ucb.proyectofinalgamerteca.features.user_library.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.user_library.data.api.dto.UserGameDto
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameReview
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.UserGame
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository
import kotlinx.coroutines.tasks.await

class UserLibraryRepository(
    private val db: FirebaseFirestore
) : IUserLibraryRepository {

    private fun getUserRef(userId: String) = db.collection("users").document(userId)

    private fun mapDocumentToUserGame(doc: com.google.firebase.firestore.DocumentSnapshot): UserGame {
        // Si el documento existe pero está vacío o corrupto, manejamos el error
        val dto = doc.toObject(UserGameDto::class.java) ?: UserGameDto()
        val id = doc.id.toLongOrNull() ?: 0L
        return dto.toModel(id)
    }

    // =============================================================================================
    // 1. GESTIÓN DE BIBLIOTECA
    // =============================================================================================

    override suspend fun addGameToLibrary(userId: String, game: GameModel, status: GameStatus?): Result<Unit> {
        return try {
            val userGameRef = getUserRef(userId).collection("library").document(game.id.toString())

            val gameData = hashMapOf(
                "name" to game.name,
                "status" to status?.name,
                "lastUpdated" to System.currentTimeMillis()
            )

            userGameRef.set(gameData, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGameStatus(userId: String, gameId: Long, status: GameStatus): Result<Unit> {
        return try {
            val ref = getUserRef(userId).collection("library").document(gameId.toString())
            ref.update(
                mapOf(
                    "status" to status.name,
                    "lastUpdated" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameFromLibrary(userId: String, gameId: Long): Result<UserGame?> {
        return try {
            val doc = getUserRef(userId).collection("library").document(gameId.toString()).get().await()
            if (doc.exists()) {
                Result.success(mapDocumentToUserGame(doc))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserGames(userId: String, status: GameStatus?): Result<List<UserGame>> {
        return try {
            var query = getUserRef(userId).collection("library")
                .orderBy("lastUpdated", Query.Direction.DESCENDING)

            if (status != null) {
                query = query.whereEqualTo("status", status.name)
            }

            val snapshot = query.get().await()
            val games = snapshot.documents.map { mapDocumentToUserGame(it) }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============================================================================================
    // 2. FILTROS ESPECIALES (Settings)
    // =============================================================================================

    override suspend fun getGamesWithAnyStatus(userId: String): Result<List<UserGame>> {
        return try {
            val snapshot = getUserRef(userId).collection("library")
                .orderBy("lastUpdated", Query.Direction.DESCENDING)
                .get().await()

            val games = snapshot.documents
                .map { mapDocumentToUserGame(it) }
                .filter { it.status != null }

            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRatedGames(userId: String): Result<List<UserGame>> {
        return try {
            val snapshot = getUserRef(userId).collection("library")
                .whereGreaterThan("userRating", 0)
                .orderBy("userRating", Query.Direction.DESCENDING)
                .get().await()

            val games = snapshot.documents.map { mapDocumentToUserGame(it) }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============================================================================================
    // 3. FAVORITOS Y RATING
    // =============================================================================================

    override suspend fun toggleFavorite(userId: String, gameId: Long, isFavorite: Boolean): Result<Unit> {
        return try {
            val ref = getUserRef(userId).collection("library").document(gameId.toString())
            val data = mapOf(
                "isFavorite" to isFavorite,
                "lastUpdated" to System.currentTimeMillis()
            )
            ref.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavorites(userId: String): Result<List<UserGame>> {
        return try {
            val snapshot = getUserRef(userId).collection("library")
                .whereEqualTo("isFavorite", true)
                .get().await()
            val games = snapshot.documents.map { mapDocumentToUserGame(it) }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setUserRating(userId: String, gameId: Long, rating: Int): Result<Unit> {
        return try {
            val ref = getUserRef(userId).collection("library").document(gameId.toString())
            val data = mapOf(
                "userRating" to rating,
                "lastUpdated" to System.currentTimeMillis()
            )
            ref.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============================================================================================
    // 4. LISTAS PERSONALIZADAS
    // =============================================================================================

    override suspend fun createCustomList(userId: String, list: CustomGameList): Result<String> {
        return try {
            val listsRef = getUserRef(userId).collection("lists")
            val newDoc = listsRef.document()
            val listToSave = list.copy(listId = newDoc.id, ownerId = userId)

            newDoc.set(listToSave).await()
            Result.success(newDoc.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getAllPublicLists(): Result<List<CustomGameList>> {
        return try {
            val snapshot = db.collectionGroup("lists")
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()

            val lists = snapshot.toObjects(CustomGameList::class.java)
            Result.success(lists)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserLists(userId: String): Result<List<CustomGameList>> {
        return try {
            val snapshot = getUserRef(userId).collection("lists").get().await()
            val lists = snapshot.toObjects(CustomGameList::class.java)
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addGameToList(userId: String, listId: String, gameId: Long): Result<Unit> {
        return try {
            getUserRef(userId).collection("lists").document(listId)
                .update("gameIds", FieldValue.arrayUnion(gameId)).await()
            getUserRef(userId).collection("library").document(gameId.toString())
                .set(mapOf("customLists" to FieldValue.arrayUnion(listId)), SetOptions.merge()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeGameFromList(userId: String, listId: String, gameId: Long): Result<Unit> {
        return try {
            // 1. Quitar ID del juego de la lista
            getUserRef(userId).collection("lists").document(listId)
                .update("gameIds", FieldValue.arrayRemove(gameId)).await()

            // 2. Quitar ID de la lista del juego
            getUserRef(userId).collection("library").document(gameId.toString())
                .update("customLists", FieldValue.arrayRemove(listId)).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCustomList(userId: String, listId: String): Result<Unit> {
        return try {
            getUserRef(userId).collection("lists").document(listId).delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============================================================================================
    // 5. STUBS
    // =============================================================================================

    override suspend fun removeGameFromLibrary(userId: String, gameId: Long): Result<Unit> {
        return try {
            getUserRef(userId).collection("library").document(gameId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // Reviews
    override suspend fun addReview(userId: String, review: GameReview): Result<String> = Result.success("")
    override suspend fun updateReview(userId: String, reviewId: String, review: GameReview): Result<Unit> = Result.success(Unit)
    override suspend fun deleteReview(userId: String, reviewId: String): Result<Unit> = Result.success(Unit)
    override suspend fun getUserReviews(userId: String): Result<List<GameReview>> = Result.success(emptyList())
    override suspend fun getGameReview(userId: String, gameId: Long): Result<GameReview?> = Result.success(null)

    // Updates
    override suspend fun updateCustomList(userId: String, listId: String, list: CustomGameList): Result<Unit> = Result.success(Unit)
}
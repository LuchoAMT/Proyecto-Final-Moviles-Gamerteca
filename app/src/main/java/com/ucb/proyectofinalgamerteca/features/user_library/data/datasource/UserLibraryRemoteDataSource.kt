package com.ucb.proyectofinalgamerteca.features.user_library.data.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ucb.proyectofinalgamerteca.features.user_library.data.api.dto.UserGameDto
import kotlinx.coroutines.tasks.await

class UserLibraryRemoteDataSource(
    private val db: FirebaseFirestore
) {
    private fun getUserRef(userId: String) = db.collection("users").document(userId)

    // Obtener un juego específico
    suspend fun getUserGame(userId: String, gameId: Long): Result<UserGameDto?> {
        return try {
            val snapshot = getUserRef(userId)
                .collection("library")
                .document(gameId.toString())
                .get()
                .await()

            if (snapshot.exists()) {
                val dto = snapshot.toObject(UserGameDto::class.java)
                Result.success(dto)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener juego de la librería"))
        }
    }

    // Guardar/Actualizar estado
    suspend fun updateGameStatus(userId: String, gameId: Long, dto: UserGameDto, oldStatus: String?): Result<Unit> {
        return try {
            db.runBatch { batch ->
                val userRef = getUserRef(userId)
                val gameRef = userRef.collection("library").document(gameId.toString())

                // 1. Actualizar el documento del juego
                batch.set(gameRef, dto, SetOptions.merge())


                if (!oldStatus.isNullOrEmpty()) {
                    val oldStatusRef = userRef.collection("statuses").document(oldStatus.lowercase())
                    batch.update(oldStatusRef, "gameIds", FieldValue.arrayRemove(gameId))
                }

                // Si tiene un estado nuevo, lo agregamos a la nueva lista
                if (!dto.status.isNullOrEmpty()) {
                    val newStatusRef = userRef.collection("statuses").document(dto.status!!.lowercase())
                    batch.set(newStatusRef, hashMapOf("gameIds" to FieldValue.arrayUnion(gameId)), SetOptions.merge())
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar rating o favorito
    suspend fun updateGameInteraction(userId: String, gameId: Long, dto: UserGameDto): Result<Unit> {
        return try {
            val ref = getUserRef(userId).collection("library").document(gameId.toString())

            ref.set(dto, SetOptions.merge()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
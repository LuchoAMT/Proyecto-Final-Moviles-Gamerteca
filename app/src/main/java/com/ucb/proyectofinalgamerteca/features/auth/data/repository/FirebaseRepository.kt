package com.ucb.proyectofinalgamerteca.features.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun registerUser(email: String, password: String, username: String): Result<Unit> {
        return try {
            // 1. Crear usuario en Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("No UID"))

            // 2. Datos Base del usuario
            val userData = mapOf(
                "email" to email,
                "username" to username,
                "createdAt" to System.currentTimeMillis()
            )

            // 3. Crear documento en /users/{uid}
            db.collection("users").document(userId).set(userData).await()

            // 4. Crear subcolección statuses con listas vacías
            val statusesRef = db.collection("users")
                .document(userId)
                .collection("statuses")

            val emptyList = mapOf("games" to emptyList<Int>())

            statusesRef.document("played").set(emptyList).await()
            statusesRef.document("playing").set(emptyList).await()
            statusesRef.document("wishlist").set(emptyList).await()
            statusesRef.document("library").set(emptyList).await()

            // 5. Crear subcolección lists con lista "favorites"
            val listsRef = db.collection("users")
                .document(userId)
                .collection("lists")

            val favoritesList = mapOf(
                "name" to "Favoritos",
                "games" to emptyList<Int>()
            )

            listsRef.document("favorites").set(favoritesList).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}

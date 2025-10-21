package com.ucb.proyectofinalgamerteca.features.games.data.datasource

import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.data.api.IgdbService
import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.GameDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class GamesRemoteDataSource(
    private val service: IgdbService,
    private val clientId: String,
    private val accessToken: String
) {
    private val TAG = "GamesRemoteDataSource"

    suspend fun getPopularGames(limit: Int = 20): Result<List<GameDto>> {
        val queryString = """
            fields id, name, rating, cover.url, first_release_date, genres.name;
            where rating > 80 & cover != null;
            sort rating desc;
            limit $limit;
        """.trimIndent()

        val requestBody = queryString.toRequestBody("text/plain".toMediaTypeOrNull())

        return try {
            val response = service.getGames(
                clientId = clientId,
                authorization = "Bearer $accessToken",
                query = requestBody
            )

            if (response.isSuccessful) {
                val games = response.body().orEmpty()
                Log.d(TAG, "✅ ${games.size} juegos recibidos de IGDB")
                Result.success(games)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e(TAG, "❌ Error ${response.code()}: $errorBody")
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getGameById(gameId: Long): Result<GameDto> {
        // ✅ QUERY COMPLETA con TODOS los campos necesarios
        val queryString = """
            fields 
                id, 
                name, 
                summary, 
                rating, 
                first_release_date,
                cover.url,
                platforms.name,
                genres.name,
                similar_games,
                involved_companies.company.name,
                involved_companies.developer,
                involved_companies.publisher,
                screenshots.url;
            where id = $gameId;
            limit 1;
        """.trimIndent()

        val requestBody = queryString.toRequestBody("text/plain".toMediaTypeOrNull())

        return try {
            val response = service.getGameDetails(
                clientId = clientId,
                authorization = "Bearer $accessToken",
                query = requestBody
            )

            if (response.isSuccessful) {
                val games = response.body().orEmpty()
                if (games.isNotEmpty()) {
                    val game = games.first()
                    Result.success(game)
                } else {
                    Log.e(TAG, "❌ Juego con ID=$gameId no encontrado en IGDB")
                    Result.failure(Exception("Juego no encontrado"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e(TAG, "❌ Error ${response.code()}: $errorBody")
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción: ${e.message}", e)
            Result.failure(e)
        }
    }
}

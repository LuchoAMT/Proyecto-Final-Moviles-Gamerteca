package com.ucb.proyectofinalgamerteca.features.games.data.datasource

import android.content.ContentValues.TAG
import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.data.api.IgdbService
import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.GameDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.flatMap

enum class SearchType {
    NAME, DEVELOPER, FRANCHISE
}
class GamesRemoteDataSource(
    private val service: IgdbService,
    private val clientId: String,
    private val accessToken: String
) {

    suspend fun searchGames(
        searchQuery: String = "",
        searchType: SearchType = SearchType.NAME,
        startDate: Long? = null,
        endDate: Long? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<GameDto>> {

        // 1. SI ES BÚSQUEDA POR DEV/FRANQUICIA, BUSCAMOS IDs PRIMERO
        val gameIdsFilter: List<Long>? = if (searchQuery.isNotBlank() && searchType != SearchType.NAME) {
            val ids = when (searchType) {
                SearchType.DEVELOPER -> searchGameIdsByDeveloper(searchQuery)
                SearchType.FRANCHISE -> searchGameIdsByFranchise(searchQuery)
                else -> emptyList()
            }
            if (ids.isEmpty()) {
                Log.w(TAG, "⚠️ No se encontraron coincidencias para: $searchQuery")
                return Result.success(emptyList())
            }
            ids
        } else {
            null
        }

        // 2. CONSTRUIR QUERY
        val queryBuilder = StringBuilder()

        // Campos
        queryBuilder.append("fields id, name, cover.url, rating, first_release_date, genres.name, platforms.name, summary;\n")

        // Condiciones
        val conditions = mutableListOf<String>()
        conditions.add("cover != null")

        // ⚠️ COMENTA ESTO SI SIGUES TENIENDO 0 RESULTADOS (A veces filtra demasiado)
        // conditions.add("category = (0, 8, 9, 10, 4)")

        // A) Filtro por Nombre
        if (searchQuery.isNotBlank() && searchType == SearchType.NAME) {
            queryBuilder.append("search \"$searchQuery\";\n")
        }

        // B) Filtro por IDs (Developer/Franchise encontrados)
        if (gameIdsFilter != null) {
            val idsString = gameIdsFilter.take(50).joinToString(",")
            conditions.add("id = ($idsString)")
        }

        // C) Filtro por Fechas
        if (startDate != null) conditions.add("first_release_date >= $startDate")
        if (endDate != null) conditions.add("first_release_date <= $endDate")

        if (conditions.isNotEmpty()) {
            queryBuilder.append("where ${conditions.joinToString(" & ")};\n")
        }

        // 3. Ordenamiento
        if (searchQuery.isBlank() || searchType != SearchType.NAME) {
            if (startDate != null) {
                queryBuilder.append("sort first_release_date desc;\n")
            } else {
                queryBuilder.append("sort rating desc;\n") // Populares
            }
        }

        queryBuilder.append("limit $limit;\n")
        queryBuilder.append("offset $offset;")

        // LOG PARA DEBUG
        Log.d(TAG, "🚀 QUERY: $queryBuilder")

        val requestBody = queryBuilder.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return try {
            val response = service.getGames(clientId, "Bearer $accessToken", requestBody)
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FUNCIONES AUXILIARES ---

    private suspend fun searchGameIdsByDeveloper(query: String): List<Long> {
        // Busca compañía y devuelve lista de IDs de juegos
        val body = "fields developed; search \"$query\"; limit 10;".toRequestBody("text/plain".toMediaTypeOrNull())
        return try {
            val res = service.searchCompanies(clientId, "Bearer $accessToken", body)
            if (res.isSuccessful) {
                res.body()?.flatMap { company -> company.developedGames ?: emptyList() } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private suspend fun searchGameIdsByFranchise(query: String): List<Long> {
        val body = "fields games; search \"$query\"; limit 10;".toRequestBody("text/plain".toMediaTypeOrNull())
        return try {
            val res = service.searchFranchises(clientId, "Bearer $accessToken", body)
            if (res.isSuccessful) {
                res.body()?.flatMap { franchise -> franchise.games ?: emptyList() } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private suspend fun executeGameQuery(query: String): Result<List<GameDto>> {
        val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
        return try {
            val response = service.getGames(clientId, "Bearer $accessToken", requestBody)
            if (response.isSuccessful) {
                val games = response.body().orEmpty()
                Log.d(TAG, "✅ Recibidos: ${games.size} juegos")
                Result.success(games)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                Log.e(TAG, "❌ Error API: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción API: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getGameById(gameId: Long): Result<GameDto> {
        val queryString = """
            fields id, name, summary, rating, first_release_date, cover.url, platforms.name, genres.name, similar_games, involved_companies.company.name, screenshots.url;
            where id = $gameId;
            limit 1;
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
                if (games.isNotEmpty()) {
                    Result.success(games.first())
                } else {
                    Result.failure(Exception("Juego no encontrado"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGamesByReleaseYear(year: Int, limit: Int) = fetchGames(
        "fields id, name, cover.url; where first_release_date >= ${dateToTimestamp("$year-01-01")} & first_release_date <= ${dateToTimestamp("$year-12-31")} & cover != null; limit $limit;"
    )

    suspend fun getGamesByGenre(genre: String, limit: Int) = fetchGames(
        "fields id, name, cover.url; where genres.name = \"$genre\" & cover != null; limit $limit;"
    )

    suspend fun getGamesByPlatform(platform: String, limit: Int) = fetchGames(
        "fields id, name, cover.url; where platforms.name = \"$platform\" & cover != null; limit $limit;"
    )

    suspend fun getGamesByDeveloper(developer: String, limit: Int) = fetchGames(
        "fields id, name, cover.url; where involved_companies.company.name = \"$developer\" & cover != null; limit $limit;"
    )

    private fun dateToTimestamp(date: String): Long {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).let { formatter ->
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(date)?.time?.div(1000) ?: 0L
        }
    }

    private suspend fun fetchGames(query: String): List<GameDto> {
        val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
        return try {
            val response = service.getGames(clientId, "Bearer $accessToken", requestBody)
            if (response.isSuccessful) response.body().orEmpty() else emptyList()
        } catch (e: Exception) { emptyList() }
    }
}

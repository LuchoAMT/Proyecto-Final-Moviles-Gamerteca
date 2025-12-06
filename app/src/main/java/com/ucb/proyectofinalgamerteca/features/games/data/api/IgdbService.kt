package com.ucb.proyectofinalgamerteca.features.games.data.api



import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.CompanySearchDto
import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.FranchiseSearchDto
import com.ucb.proyectofinalgamerteca.features.games.data.api.dto.GameDto
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IgdbService {
    @POST("games")
    suspend fun getGames(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body query: RequestBody
    ): Response<List<GameDto>>

    @POST("companies")
    suspend fun searchCompanies(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body query: okhttp3.RequestBody
    ): retrofit2.Response<List<CompanySearchDto>>

    @POST("franchises")
    suspend fun searchFranchises(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body query: okhttp3.RequestBody
    ): retrofit2.Response<List<FranchiseSearchDto>>

}

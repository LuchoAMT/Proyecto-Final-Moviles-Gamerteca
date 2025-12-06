package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName

data class CompanySearchDto(
    val id: Long,
    val name: String?,
    @SerializedName("developed") val developedGames: List<Long>? = null
)
package com.ucb.proyectofinalgamerteca.features.games.data.api.dto

import com.google.gson.annotations.SerializedName

data class InvolvedCompanyDto(
    @SerializedName("company") val company: CompanyDto?,
    @SerializedName("developer") val developer: Boolean,
    @SerializedName("publisher") val publisher: Boolean
)
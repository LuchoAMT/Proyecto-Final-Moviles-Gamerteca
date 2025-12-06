package com.ucb.proyectofinalgamerteca.features.games.domain.usecase

import com.ucb.proyectofinalgamerteca.features.games.data.datasource.SearchType
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository

class SearchGamesUseCase(private val repository: IGamesRepository) {
    suspend operator fun invoke(
        query: String = "",
        searchType: SearchType = SearchType.NAME,
        startDate: Long? = null,
        endDate: Long? = null,
        offset: Int = 0
    ) = repository.searchGames(query, searchType, startDate, endDate, offset)
}
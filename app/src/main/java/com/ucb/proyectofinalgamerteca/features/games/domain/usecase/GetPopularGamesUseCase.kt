package com.ucb.proyectofinalgamerteca.features.games.domain.usecase

import android.util.Log
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository

class GetPopularGamesUseCase(
    private val repository: IGamesRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<GameModel>> {
        Log.d("GetPopularGamesUseCase", "Obteniendo juegos populares...")
        return repository.getPopularGames(forceRefresh)
    }
}
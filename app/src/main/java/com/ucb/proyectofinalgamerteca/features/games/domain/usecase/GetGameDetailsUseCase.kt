package com.ucb.proyectofinalgamerteca.features.games.domain.usecase

import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository

class GetGameDetailsUseCase(
    private val repository: IGamesRepository
) {
    suspend operator fun invoke(gameId: Long): Result<GameModel> {
        return repository.getGameById(gameId)
    }
}
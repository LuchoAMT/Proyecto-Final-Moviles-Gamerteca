package com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase

import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameReview
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository

class AddGameToLibraryUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, game: GameModel, status: GameStatus?) =
        repository.addGameToLibrary(userId, game, status)
}

class GetUserGameInteractionUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, gameId: Long) =
        repository.getGameFromLibrary(userId, gameId)
}

class ToggleFavoriteUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, gameId: Long, isFavorite: Boolean) =
        repository.toggleFavorite(userId, gameId, isFavorite)
}

// --- Listas Personalizadas ---

class CreateCustomListUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, list: CustomGameList) =
        repository.createCustomList(userId, list)
}

class GetUserListsUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String) = repository.getUserLists(userId)
}

class AddGameToListUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, listId: String, gameId: Long) =
        repository.addGameToList(userId, listId, gameId)
}

// --- Reseñas ---

class AddReviewUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, review: GameReview) =
        repository.addReview(userId, review)
}

class SetUserRatingUseCase(private val repository: IUserLibraryRepository) {
    suspend operator fun invoke(userId: String, gameId: Long, rating: Int) =
        repository.setUserRating(userId, gameId, rating)
}
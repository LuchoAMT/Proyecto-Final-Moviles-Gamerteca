package com.ucb.proyectofinalgamerteca.navigation

sealed class Screen(val route: String) {
    object Startup: Screen("startup")
    object Login: Screen("login")
    object Register: Screen("register")
    object Home: Screen("home")
    object GamesList : Screen("games_list")
    object GameDetail : Screen("game_detail/{gameId}") {
        fun createRoute(gameId: Long) = "game_detail/$gameId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object PlatformList : Screen("platform_list/{platform}"){
        fun createRoute(platform: String) = "platform_list/$platform"
    }
    object GenreList : Screen("genre_list/{genre}") {
        fun createRoute(genre: String) = "genre_list/$genre"
    }
    object ReleaseYearList : Screen("release_year_list/{year}") {
        fun createRoute(year: Int) = "release_year_list/$year"
    }
    object DeveloperList : Screen("developer_list/{developer}") {
        fun createRoute(developer: String) = "developer_list/$developer"
    }
    // Ruta dinámica para el tipo de filtro (favorites, played, owned, etc.)
    object UserGames : Screen("user_games/{filter}") {
        fun createRoute(filter: String) = "user_games/$filter"
    }

    object UserLists : Screen("user_lists")

    object ListDetail : Screen("list_detail/{listId}") {
        fun createRoute(listId: String) = "list_detail/$listId"
    }
}
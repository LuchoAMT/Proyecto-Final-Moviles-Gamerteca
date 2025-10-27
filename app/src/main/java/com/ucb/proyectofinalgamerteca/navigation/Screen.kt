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
}
package com.ucb.proyectofinalgamerteca.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.proyectofinalgamerteca.features.games.presentation.GamesListScreen
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginScreen
import com.ucb.proyectofinalgamerteca.features.register.presentation.RegisterScreen
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupScreen

@Composable
fun AppNavigation(startDestination: String = Screen.GamesList.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        // Pantalla de Inicio
        composable(Screen.Startup.route) {
            StartupScreen(
              onContinue = { navController.navigate(Screen.Login.route) }
            )
        }

        // Pantalla de login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Pantalla de registro
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de lista de juegos
        composable(Screen.GamesList.route) {
            GamesListScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.GameDetail.createRoute(gameId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            //TODO HomeScreen()
        }
    }
}
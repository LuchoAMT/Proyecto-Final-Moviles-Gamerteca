package com.ucb.proyectofinalgamerteca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupScreen
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(startDestination: String = "startup") {
    val navController = rememberNavController()
    NavHost(navController, startDestination = startDestination) {
        composable("startup") {
            StartupScreen(
              onContinue = { navController.navigate("login") }
            )
        }
    }
//    composable("login") {
//        LoginScreen()
//    }
}
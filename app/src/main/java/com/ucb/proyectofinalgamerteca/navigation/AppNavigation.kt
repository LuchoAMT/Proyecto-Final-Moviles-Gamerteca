package com.ucb.proyectofinalgamerteca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginScreen
import com.ucb.proyectofinalgamerteca.features.register.presentation.RegisterScreen
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupScreen
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(startDestination: String = Screen.Startup.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Startup.route) {
            StartupScreen(
              onContinue = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) },
                onNavigateToLogin = { navController.navigate(navController.popBackStack()) }
            )
        }

        composable(Screen.Home.route) {
            //TODO HomeScreen()
        }
    }
}
package com.ucb.proyectofinalgamerteca.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.DeveloperGamesScreen
import com.ucb.proyectofinalgamerteca.features.games.presentation.detail.GameDetailScreen
import com.ucb.proyectofinalgamerteca.features.games.presentation.home.GamesListScreen
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.GenreGamesScreen
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.PlatformGamesScreen
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.lists_public.PublicListsScreen
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.ReleaseYearGamesScreen
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginScreen
import com.ucb.proyectofinalgamerteca.features.settings.presentation.ProfileScreen
import com.ucb.proyectofinalgamerteca.features.register.presentation.RegisterScreen
import com.ucb.proyectofinalgamerteca.features.settings.presentation.SettingsScreen
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupScreen
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.list_detail.ListDetailScreen
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.my_collection.UserGamesScreen
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.lists_manager.UserListsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rutas donde NO queremos mostrar la barra inferior
    val noBottomBarRoutes = listOf(
        Screen.Startup.route,
        Screen.Login.route,
        Screen.Register.route,
    )

    Scaffold(
        bottomBar = {
            if (currentRoute !in noBottomBarRoutes) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Startup
            composable(Screen.Startup.route) {
                StartupScreen(onContinue = { navController.navigate(Screen.Login.route) })
            }

            // Login
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Screen.GamesList.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            // Register
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.GamesList.route) {
                          popUpTo(Screen.Startup.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Lista de juegos
            composable(Screen.GamesList.route) {
                GamesListScreen(
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Detalle del juego
            composable(
                route = Screen.GameDetail.route,
                arguments = listOf(navArgument("gameId") { type = NavType.LongType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
                GameDetailScreen(
                    gameId = gameId,
                    onBackClick = { navController.popBackStack() },
                    onGameClick = { id -> navController.navigate(Screen.GameDetail.createRoute(id)) },
                    onGameClickGenre = { genre -> navController.navigate(Screen.GenreList.createRoute(genre)) },
                    onGameClickPlatform = { platform -> navController.navigate(Screen.PlatformList.createRoute(platform)) },
                    onGameClickReleaseYear = { year -> navController.navigate(Screen.ReleaseYearList.createRoute(year)) },
                    onGameClickDeveloper = { developer -> navController.navigate(Screen.DeveloperList.createRoute(developer)) }
                )
            }

            // Pantallas de navegación inferior
            composable(Screen.Home.route) {
                GamesListScreen(
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("lists") {
                PublicListsScreen(
                    onListClick = { listId ->
                        navController.navigate(Screen.ListDetail.createRoute(listId))
                    }
                )
            }

            composable(Screen.UserLists.route) {
                UserListsScreen(
                    onBackClick = { navController.popBackStack() },
                    onListClick = { listId ->
                        navController.navigate(Screen.ListDetail.createRoute(listId))
                    }
                )
            }

            composable(
                route = Screen.ListDetail.route,
                arguments = listOf(navArgument("listId") { type = NavType.StringType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getString("listId") ?: return@composable
                ListDetailScreen(
                    listId = listId,
                    onBackClick = { navController.popBackStack() },
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    // Navegación a listas
                    onNavigateToUserLibrary = { filter ->
                        if (filter == "lists") {
                            navController.navigate(Screen.UserLists.route)
                        } else {
                            navController.navigate(Screen.UserGames.createRoute(filter))
                        }
                    }
                )
            }

            composable(
                Screen.PlatformList.route,
                arguments = listOf(navArgument("platform") { type = NavType.StringType })
            )
            { backStackEntry ->
                val platform = backStackEntry.arguments?.getString("platform") ?: return@composable
                PlatformGamesScreen(
                    platformName = platform,
                    onGameClick = { gameId ->
                        navController.navigate(
                            Screen.GameDetail.createRoute(
                                gameId
                            )
                        )
                    },
                    onBackClick = { navController.popBackStack() },

                    )
            }

            composable(
                Screen.GenreList.route,
                arguments = listOf(navArgument("genre") { type = NavType.StringType })
            ) { backStackEntry ->
                val genre = backStackEntry.arguments?.getString("genre") ?: return@composable
                GenreGamesScreen(
                    genre = genre,
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                Screen.ReleaseYearList.route,
                arguments = listOf(navArgument("year") { type = NavType.IntType })
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year") ?: return@composable
                ReleaseYearGamesScreen(
                    year = year,
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                Screen.DeveloperList.route,
                arguments = listOf(navArgument("developer") { type = NavType.StringType })
            ) { backStackEntry ->
                val developer = backStackEntry.arguments?.getString("developer") ?: return@composable
                DeveloperGamesScreen(
                    developer = developer,
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.UserGames.route,
                arguments = listOf(navArgument("filter") { type = NavType.StringType })
            ) { backStackEntry ->
                val filter = backStackEntry.arguments?.getString("filter") ?: "owned"
                UserGamesScreen(
                    filter = filter,
                    onBackClick = { navController.popBackStack() },
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    }
                )
            }

        }
    }
}
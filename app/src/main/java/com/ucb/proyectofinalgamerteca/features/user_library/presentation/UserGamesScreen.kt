package com.ucb.proyectofinalgamerteca.features.user_library.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.GameCard
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGamesScreen(
    filter: String,
    onBackClick: () -> Unit,
    onGameClick: (Long) -> Unit,
    viewModel: UserGamesViewModel = koinViewModel()
) {
    val sections by viewModel.sections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(filter) {
        viewModel.loadGames(filter)
    }

    val screenTitle = when(filter) {
        "favorites" -> "Favoritos"
        "reviews" -> "Mis Reseñas"
        "owned", "all" -> "Mi Colección"
        else -> "Juegos"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        color = Color(0xFFE52128),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFFE52128))
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFE52128))
            } else if (sections.isEmpty() || sections.all { it.games.isEmpty() }) {
                // Mensaje si no hay nada
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.SentimentDissatisfied, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No hay juegos aquí aún", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 Columnas fijas
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    sections.forEach { section ->

                        // 1. HEADER DE SECCIÓN
                        if (section.title.isNotEmpty() && section.games.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = section.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                        }

                        // 2. JUEGOS DE LA SECCIÓN
                        items(section.games) { userGame ->
                            // Cálculo del rating para mostrar el badge
                            val myRatingScore = (userGame.userRating ?: 0).toDouble() * 20.0

                            val gameModel = GameModel(
                                id = userGame.gameId,
                                name = userGame.name,
                                coverUrl = userGame.coverUrl,
                                rating = myRatingScore,
                                summary = "", releaseDate = null, platforms = emptyList(), genres = emptyList()
                            )

                            GameCard(
                                game = gameModel,
                                onClick = { onGameClick(userGame.gameId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Long,
    modifier: Modifier = Modifier,
    vm: GameDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(gameId) {
        vm.loadGameDetails(gameId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Gamerteca") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is GameDetailViewModel.UiState.Init -> {
                    Text("Iniciando...", modifier = Modifier.align(Alignment.Center))
                }
                is GameDetailViewModel.UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is GameDetailViewModel.UiState.Error -> {
                    Text("Error: ${s.message}", modifier = Modifier.align(Alignment.Center))
                }
                is GameDetailViewModel.UiState.Success -> {
                    GameDetailContent(game = s.game)
                }
            }
        }
    }
}

@Composable
private fun GameDetailContent(game: GameModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sección: Portada e información básica
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Portada (lado izquierdo)
                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    AsyncImage(
                        model = game.getHighResolutionCoverUrl(),
                        contentDescription = game.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Información básica (lado derecho)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título del juego
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Fecha de lanzamiento
                    Text(
                        text = "Fecha de lanzamiento:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = game.getFormattedReleaseDate(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Desarrollador (placeholder - IGDB no devuelve esto en la query básica)
                    Text(
                        text = "Desarrollador:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Información no disponible",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Sección: Rating (estrellas)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Valoración",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val stars = game.getRatingStars()
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < stars) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        game.rating?.let {
                            Text(
                                text = "${String.format("%.1f", it / 20)}/5",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        // Sección: Resumen del juego
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = game.summary ?: "Sin descripción disponible",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Sección: Géneros
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Géneros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (game.genres.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            game.genres.take(3).forEach { genre ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(genre) }
                                )
                            }
                        }
                    } else {
                        Text("No hay géneros disponibles")
                    }
                }
            }
        }

        // Sección: Plataformas
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Plataformas para jugar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (game.platforms.isNotEmpty()) {
                        game.platforms.forEach { platform ->
                            Text(
                                text = "• $platform",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    } else {
                        Text("No hay plataformas disponibles")
                    }
                }
            }
        }

        // Sección: Juegos relacionados
/*        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Juegos relacionados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (game.similarGames.isNotEmpty()) {
                        Text(
                            text = "${game.similarGames.size} juegos similares encontrados",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "IDs: ${game.similarGames.take(5).joinToString(", ")}${if (game.similarGames.size > 5) "..." else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text("No hay juegos similares disponibles")
                    }
                }
            }
        }*/

        // Sección: Reseñas (placeholder)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reseñas del juego",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Las reseñas no están disponibles en esta versión. Para implementar reseñas necesitarías conectar con otra API o crear un sistema propio.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
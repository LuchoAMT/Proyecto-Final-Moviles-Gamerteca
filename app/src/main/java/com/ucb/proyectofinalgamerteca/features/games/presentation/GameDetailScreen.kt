package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Long,
    modifier: Modifier = Modifier,
    vm: GameDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onGameClick: (Long) -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(gameId) {
        vm.loadGameDetails(gameId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Gamerteca",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE52128)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFFE52128)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFFE52128),
                    navigationIconContentColor = Color(0xFFE52128)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val currentState = state) {
                is GameDetailViewModel.UiState.Init -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is GameDetailViewModel.UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is GameDetailViewModel.UiState.Error -> {
                    Text("Error: ${currentState.message}", modifier = Modifier.align(Alignment.Center))
                }
                is GameDetailViewModel.UiState.Success -> {
                    GameDetailContent(game = currentState.game, onGameClick = onGameClick)
                }
            }
        }
    }
}

@Composable
fun GameDetailContent(game: GameModel, modifier: Modifier = Modifier, onGameClick: (Long) -> Unit) {
    val scrollState = rememberScrollState()
    val randomScreenshot = remember(game.screenshots) {
        if (game.screenshots.isNotEmpty()) {
            game.screenshots.random()
        } else {
            ""
        }
    }
    var userRating by remember { mutableStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Fondo difuminado con una captura de pantalla del juego
        if (randomScreenshot.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(randomScreenshot)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.5f }
                    .blur(radius = 16.dp)
            )
        } else {
            // Fondo de respaldo si no hay captura de pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
            )
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Sección: Portada e información básica
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(game.getHighResolutionCoverUrl() ?: game.coverUrl)
                                .crossfade(true)
                                .build(),
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
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Desarrolladores
                        if (game.involvedCompanies.isNotEmpty()) {
                            Text(
                                text = "Desarrolladores: ${game.involvedCompanies.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        // Calificación
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = game.getRatingText(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Botones de estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameStatusButton(
                    text = "Jugado",
                    icon = Icons.Default.Gamepad,
                    color = Color(0xFF81C784),
                    modifier = Modifier.weight(1f)
                )
                GameStatusButton(
                    text = "Jugando",
                    icon = Icons.Default.PlayArrow,
                    color = Color(0xFF64B5F6),
                    modifier = Modifier.weight(1f)
                )
                GameStatusButton(
                    text = "Biblioteca",
                    icon = Icons.Default.LibraryBooks,
                    color = Color(0xFFEF9A9A),
                    modifier = Modifier.weight(1f)
                )
                GameStatusButton(
                    text = "Lo quiero",
                    icon = Icons.Default.CardGiftcard,
                    color = Color(0xFFEF9A9A),
                    modifier = Modifier.weight(1f)
                )
            }

            // Sección: Calificación y Favoritos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Calificación con estrellas (interactiva)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { index ->
                            Icon(
                                imageVector = if (index <= userRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index <= userRating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { userRating = index }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${if (userRating > 0) userRating else game.getRatingStars()}/5",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    // Botón de favoritos (interactivo)
                    IconToggleButton(
                        checked = isFavorite,
                        onCheckedChange = { isFavorite = it }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                }
            }

            // Sección: Resumen del juego
            if (!game.summary.isNullOrEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resumen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE52128)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = game.summary ?: "Sin descripción disponible",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Sección: Detalles del juego
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Fecha de lanzamiento
                    if (game.releaseDate != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Fecha de lanzamiento",
                                tint = Color(0xFFE52128)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fecha de lanzamiento: ${game.getFormattedReleaseDate()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Divider()
                    }

                    // Géneros
                    if (game.genres.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = "Géneros",
                                tint = Color(0xFFE52128)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Géneros:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = game.genres.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Divider()
                    }

                    // Plataformas
                    if (game.platforms.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (game.platforms.any { it.equals("Linux", ignoreCase = true) || it.equals("Mac", ignoreCase = true) || it.equals("Windows", ignoreCase = true) })
                                    Icons.Default.Computer else Icons.Default.Gamepad,
                                contentDescription = "Plataformas",
                                tint = Color(0xFFE52128)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Plataformas:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (game.platforms.isNotEmpty()) game.platforms.joinToString(", ") else "No hay información de plataformas disponible",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Text("No hay información de plataformas disponible")
                    }
                }
            }

            // Sección: Juegos relacionados
            if (game.similarGames.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Juegos Relacionados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE52128)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(game.similarGames.size) { index ->
                                val relatedGameId = game.similarGames[index]
                                RelatedGameCard(
                                    gameId = relatedGameId,
                                    onClick = { onGameClick(relatedGameId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun GameStatusButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isSelected by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isSelected = !isSelected }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RelatedGameCard(gameId: Long, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            // Aquí debes cargar la imagen de la portada del juego relacionado
            // Por ahora, solo mostramos el ID como placeholder
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = "Juego relacionado",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Juego $gameId",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
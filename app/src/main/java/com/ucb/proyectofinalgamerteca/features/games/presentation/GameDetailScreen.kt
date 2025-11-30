package com.ucb.proyectofinalgamerteca.features.games.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ucb.proyectofinalgamerteca.R
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.GameStatus
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Long,
    modifier: Modifier = Modifier,
    vm: GameDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onGameClick: (Long) -> Unit,
    onGameClickGenre: (String) -> Unit,
    onGameClickPlatform: (String) -> Unit,
    onGameClickReleaseYear: (Int) -> Unit,
    onGameClickDeveloper: (String) -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(gameId) {
        vm.loadGameDetails(gameId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(120.dp),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_gamerteca),
                        contentDescription = "Gamerteca Logo",
                        modifier = Modifier.height(200.dp)
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
        ) {
            when (val currentState = state) {
                is GameDetailViewModel.UiState.Init -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFE52128)
                    )
                }
                is GameDetailViewModel.UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFE52128)
                    )
                }
                is GameDetailViewModel.UiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Error al cargar el juego",
             
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            currentState.message,
             
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                is GameDetailViewModel.UiState.Success -> {
                    GameDetailContent(
                        game = currentState.game,
                        onGameClick = onGameClick,
                        onGameClickGenre = onGameClickGenre,
                        onGameClickPlatform = onGameClickPlatform,
                        onGameClickReleaseYear = onGameClickReleaseYear,
                        onGameClickDeveloper = onGameClickDeveloper,
                        viewModel = vm,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun GameDetailContent(
    game: GameModel,
    onGameClick: (Long) -> Unit,
    onGameClickGenre: (String) ->Unit,
    onGameClickPlatform: (String) ->Unit,
    onGameClickReleaseYear: (Int) -> Unit,
    onGameClickDeveloper: (String) -> Unit,
    viewModel: GameDetailViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val userState by viewModel.userState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Sección: Portada e información básica
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Portada
                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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

                // Información básica
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = game.name,
         
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    if (game.involvedCompanies.isNotEmpty()) {
                        Text(
                            text = "Desarrolladores:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        LazyRow(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(game.involvedCompanies) { developer ->
                                Text(
                                    text = developer,
                                    fontSize = 14.sp,
                                    color = Color.Blue,
                                    modifier = Modifier
                                        .clickable { onGameClickDeveloper(developer) }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

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
             
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
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
                isSelected = userState.status == GameStatus.PLAYED,
                onClick = { viewModel.onStatusSelected(GameStatus.PLAYED)},
                modifier = Modifier.weight(1f)
            )
            GameStatusButton(
                text = "Jugando",
                icon = Icons.Default.PlayArrow,
                color = Color(0xFF64B5F6),
                isSelected = userState.status == GameStatus.PLAYING,
                onClick = { viewModel.onStatusSelected(GameStatus.PLAYING)},
                modifier = Modifier.weight(1f)
            )
            GameStatusButton(
                text = "Biblioteca",
                icon = Icons.Default.LibraryBooks,
                color = Color(0xFFFFEB3B),
                isSelected = userState.status == GameStatus.OWNED,
                onClick = { viewModel.onStatusSelected(GameStatus.OWNED)},
                modifier = Modifier.weight(1f)
            )
            GameStatusButton(
                text = "Lo quiero",
                icon = Icons.Default.CardGiftcard,
                color = Color(0xFFEF9A9A),
                isSelected = userState.status == GameStatus.WISHLIST,
                onClick = { viewModel.onStatusSelected(GameStatus.WISHLIST )},
                modifier = Modifier.weight(1f)
            )
        }

        // Rating y Favorito

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sección de Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    (1..5).forEach { index ->
                        Icon(
                            imageVector = if (index <= userState.userRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index <= userState.userRating) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { viewModel.onRatingChanged(index) }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Botón de Favoritos
                IconToggleButton(
                    checked = userState.isFavorite,
                    onCheckedChange = { viewModel.onToggleFavorite() }
                ) {
                    Icon(
                        imageVector = if (userState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (userState.isFavorite) Color(0xFFE52128) else Color.Gray
                    )
                }
            }
        }

        // ✅ Resumen
        if (!game.summary.isNullOrEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFE52128)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = game.summary,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Detalles del juego
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Fecha
                if (game.releaseDate != null) {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = game.releaseDate * 1000
                    val year = calendar.get(Calendar.YEAR)
                    Row(
                        modifier = Modifier
                            .clickable { onGameClickReleaseYear(year) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Fecha de lanzamiento",
                            tint = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Fecha de lanzamiento: ${game.getFormattedReleaseDate()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue,  // Cambia el color a azul
                            modifier = Modifier
                                .drawBehind {
                                    drawLine(
                                        color = Color.Blue,
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 2f
                                    )
                                }
                        )
                    }
                    Divider(color = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                }

                // Géneros
                if (game.genres.isNotEmpty()) {
                    Text(
                        text = "Géneros",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(game.genres) { genreName ->
                            GenreChip(
                                genreName = genreName,
                                onClick = { selectedGenre -> onGameClickGenre(selectedGenre) }
                            )
                        }
                    }
                }

                // Plataformas
                if (game.platforms.isNotEmpty()) {
                    Text(
                        text = "Plataformas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(game.platforms) { platformName ->
                            val icon = when (platformName.lowercase()) {
                                "PC (Microsoft Windows)", "mac", "linux" -> Icons.Default.Computer
                                else -> Icons.Default.Gamepad
                            }
                            PlatformChip(platformName = platformName, icon = icon, onClick = { selectedPlatform -> onGameClickPlatform(selectedPlatform) })
                        }
                    }
                }
            }
        }

        // ✅ CAPTURAS DE PANTALLA
        if (game.screenshots.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Capturas de pantalla",
         
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFE52128)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(game.screenshots) { screenshot ->
                            Card(
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(170.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(screenshot.replace("t_thumb", "t_screenshot_med"))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Screenshot",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        // ✅ JUEGOS RELACIONADOS CON PORTADAS
        if (game.similarGames.isNotEmpty()) {
            val relatedGames by viewModel.relatedGames.collectAsState()

            LaunchedEffect(game.similarGames) {
                viewModel.loadRelatedGames(game.similarGames.take(10))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Juegos relacionados",
         
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFE52128)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (relatedGames.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFFE52128)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(relatedGames) { relatedGame ->
                                RelatedGameCard(
                                    game = relatedGame,
                                    onClick = { onGameClick(relatedGame.id) }
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
    isSelected: Boolean,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

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
                .clickable { onClick() }
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
                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GenreChip(
    genreName: String,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .height(40.dp)
            .padding(4.dp)
            .clickable {
                onClick(genreName.trim())
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF9E9E9E))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = genreName,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}


@Composable
fun PlatformChip(
    platformName: String,
    icon: ImageVector,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .height(40.dp)
            .padding(4.dp)
            .clickable { onClick(platformName) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF9E9E9E))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFBB86FC),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = platformName,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RelatedGameCard(
    game: GameModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(game.getHighResolutionCoverUrl() ?: game.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = game.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Nombre en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                Text(
                    text = game.name,
                    fontSize = 11.sp,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
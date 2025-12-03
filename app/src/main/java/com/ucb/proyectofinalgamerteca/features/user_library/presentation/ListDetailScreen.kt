package com.ucb.proyectofinalgamerteca.features.user_library.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.ucb.proyectofinalgamerteca.features.games.presentation.GameCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    onBackClick: () -> Unit,
    onGameClick: (Long) -> Unit,
    vm: ListDetailViewModel = koinViewModel()
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(listId) {
        vm.loadList(listId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalle de Lista",
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

            if (state.isLoading && state.listInfo == null) {
                // Carga inicial (pantalla blanca con spinner)
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFE52128))
            } else if (state.error != null) {
                // Error
                Text(
                    text = state.error ?: "Error desconocido",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else if (state.listInfo != null) {
                // CONTENIDO
                val list = state.listInfo!!

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // --- CABECERA (Span 2) ---
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), MaterialTheme.shapes.medium)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = list.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            if (list.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = list.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Por: ${list.ownerName.ifBlank { "Anónimo" }}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${list.gameIds.size} juegos",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE52128),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // --- JUEGOS ---
                    items(state.games) { game ->
                        GameCard(
                            game = game,
                            onClick = { onGameClick(game.id) }
                        )
                    }

                    // Espacio abajo
                    item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
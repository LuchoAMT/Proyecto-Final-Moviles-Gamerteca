package com.ucb.proyectofinalgamerteca.features.games.presentation.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.SearchType
import com.ucb.proyectofinalgamerteca.features.games.presentation.components.GameCard
import com.ucb.proyectofinalgamerteca.ui.theme.RedPrimary
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    modifier: Modifier = Modifier,
    vm: GamesListViewModel = koinViewModel(),
    onGameClick: (Long) -> Unit,
) {
    val state by vm.uiState.collectAsState()
    var queryText by remember { mutableStateOf("") }

    // Estados locales para refrescar UI de filtros
    val isDateActive = vm.isDateFilterActive
    val currentType = vm.currentSearchType

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 8.dp)
            ) {
                // 1. Barra Superior
                CenterAlignedTopAppBar(
                    title = { Text("Gamerteca", fontWeight = FontWeight.Bold, color = RedPrimary) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )

                // 2. Buscador
                OutlinedTextField(
                    value = queryText,
                    onValueChange = {
                        queryText = it
                        vm.onSearchQueryChanged(it)
                    },
                    placeholder = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        cursorColor = RedPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. FILTROS
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip de Fecha (Último Año)
                    item {
                        FilterChip(
                            selected = isDateActive,
                            onClick = { vm.onDateFilterToggle() },
                            label = { Text("Último año") },
                            leadingIcon = if (isDateActive) {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                            } else { { Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(16.dp)) } },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = RedPrimary
                            )
                        )
                    }

                    // Chip Tipo: Nombre
                    item {
                        FilterChip(
                            selected = currentType == SearchType.NAME,
                            onClick = { vm.onSearchTypeChanged(SearchType.NAME) },
                            label = { Text("Nombre") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.LightGray.copy(alpha=0.5f))
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.games.isEmpty() && state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedPrimary)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Items Juegos
                    items(state.games) { game ->
                        GameCard(
                            game = game,
                            onClick = { onGameClick(game.id) }
                        )
                    }

                    // Botón "Ver Más"
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = RedPrimary)
                            } else {
                                Button(
                                    onClick = { vm.onLoadMore() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RedPrimary)
                                ) {
                                    Text("Ver más", color = RedPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
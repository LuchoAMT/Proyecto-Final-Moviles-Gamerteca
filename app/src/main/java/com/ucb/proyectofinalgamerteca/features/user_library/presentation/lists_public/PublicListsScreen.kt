package com.ucb.proyectofinalgamerteca.features.user_library.presentation.lists_public

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.components.GameListCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicListsScreen(
    onListClick: (String) -> Unit,
    vm: PublicListsViewModel = koinViewModel()
) {
    val state by vm.uiState.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()
    val query by vm.searchQuery.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = true
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                // 1. Buscador
                OutlinedTextField(
                    value = query,
                    onValueChange = { vm.onSearchQueryChanged(it) },
                    placeholder = { Text("Buscar listas...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE52128),
                        cursorColor = Color(0xFFE52128)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Filtros y Contador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de Filtro (Simulado o funcional)
                    AssistChip(
                        onClick = { /* Abrir DatePicker o menú */ },
                        label = { Text("Filtrar por fecha") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )

                    // Contador de resultados
                    Text(
                        text = "${state.size} resultados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFFE52128),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Lista")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Items normales
                items(state) { item ->
                    GameListCard(
                        list = item.list,
                        coverUrls = item.coverUrls,
                        onClick = { onListClick(item.list.listId) }
                    )
                }

                // --- BOTÓN VER MÁS ---
                item {
                    if (state.isNotEmpty()) { // Solo mostrar si hay listas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoadingMore) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFE52128))
                            } else {
                                TextButton(
                                    onClick = { vm.loadPublicLists(initialLoad = false) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE52128))
                                ) {
                                    Text("Ver más", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        // Espacio extra para que el FAB no tape el último botón
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}
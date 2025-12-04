package com.ucb.proyectofinalgamerteca.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToUserLibrary: (String) -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var isDarkMode by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = false
    )


    Scaffold(
        // Importante: contentWindowInsets = 0.dp para que el Scaffold NO empuje
        // el contenido hacia abajo automáticamente. Queremos controlar eso nosotros
        // para que el rojo quede detrás de la barra.
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding inferior del sistema (si hubiera barra de navegación)
                .background(Color.White)
        ) {
            // --- CABECERA DE PERFIL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.height(240.dp) // Un poco más alto para que quepa el botón
                    .background(Color(0xFFE53935)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.statusBarsPadding().padding(bottom = 24.dp)) {
                    // 1. Imagen Genérica (Local)
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Imagen de perfil",

                        tint = Color(0xFFE53935),

                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Datos del Usuario
                    Text(
                        text = state.userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = state.email,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Botón para ir a Editar Perfil
                    Button(
                        onClick = onNavigateToProfile,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFE53935)
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Editar Perfil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- OPCIONES (ATAJOS) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Mi Biblioteca",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SettingsItem(
                    icon = Icons.Default.SportsEsports,
                    title = "Mis Juegos",
                    onClick = { onNavigateToUserLibrary("all") }
                )
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = "Mis Listas",
                    onClick = { onNavigateToUserLibrary("lists") }
                )
                SettingsItem(
                    icon = Icons.Default.FavoriteBorder,
                    title = "Favoritos",
                    onClick = { onNavigateToUserLibrary("favorites") }
                )
                SettingsItem(
                    icon = Icons.Default.Star,
                    title = "Mis Reseñas",
                    onClick = { onNavigateToUserLibrary("reviews") }
                )

                Divider(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "Configuración",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Lenguaje",
                    onClick = onNavigateToLanguage
                )
                SettingsItem(
                    icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = if (isDarkMode) "Modo claro" else "Modo oscuro",
                    onClick = {
                        isDarkMode = !isDarkMode
                        onToggleDarkMode(isDarkMode)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}
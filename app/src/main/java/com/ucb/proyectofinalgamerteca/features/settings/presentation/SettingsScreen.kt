package com.ucb.proyectofinalgamerteca.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ucb.proyectofinalgamerteca.R

@Composable
fun SettingsScreen(
    userName: String = "test",
    email: String = "test@gmail.com",
    profileImageUrl: String? = null,
    onNavigateToReviews: () -> Unit = {},
    onNavigateToGames: () -> Unit = {},
    onNavigateToLists: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit = {}
) {
    var isDarkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFE53935)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = profileImageUrl ?: R.drawable.v1_default_profile,
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = email,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lista de opciones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Atajos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingsItem(
                icon = Icons.Default.Star,
                title = "Mis Reseñas",
                onClick = onNavigateToReviews
            )
            SettingsItem(
                icon = Icons.Default.SportsEsports,
                title = "Mis Juegos",
                onClick = onNavigateToGames
            )
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.List,
                title = "Mis Listas",
                onClick = onNavigateToLists
            )
            SettingsItem(
                icon = Icons.Default.FavoriteBorder,
                title = "Favoritos",
                onClick = onNavigateToFavorites
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
            tint = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ir a $title",
            tint = Color.Gray
        )
    }
}
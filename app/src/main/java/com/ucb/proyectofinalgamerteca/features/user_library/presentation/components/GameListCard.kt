package com.ucb.proyectofinalgamerteca.features.user_library.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList

@Composable
fun GameListCard(
    list: CustomGameList,
    coverUrls: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF0F0F0))
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (coverUrls.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Vacía", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    }
                } else {
                    // Renderizamos de atrás hacia adelante

                    // Imagen 3 (Fondo - Derecha)
                    if (coverUrls.size > 2) {
                        CollageImage(
                            url = coverUrls[2],
                            modifier = Modifier
                                .fillMaxHeight(0.85f)
                                .aspectRatio(0.7f)
                                .offset(x = 60.dp)
                                .zIndex(1f)
                        )
                    }

                    // Imagen 2 (Medio)
                    if (coverUrls.size > 1) {
                        CollageImage(
                            url = coverUrls[1],
                            modifier = Modifier
                                .fillMaxHeight(0.92f)
                                .aspectRatio(0.7f)
                                .offset(x = 30.dp)
                                .zIndex(2f)
                        )
                    }

                    // Imagen 1 (Frente - Izquierda)
                    if (coverUrls.isNotEmpty()) {
                        CollageImage(
                            url = coverUrls[0],
                            modifier = Modifier
                                .fillMaxHeight(1f)
                                .aspectRatio(0.7f)
                                .zIndex(3f)
                        )
                    }
                }
            }

            // --- SECCIÓN DERECHA: INFORMACIÓN ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                // Descripción
                if (list.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = list.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${list.gameIds.size} juegos",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (list.ownerName.isNotBlank()) {
                    Text(
                        text = "Creado por: ${list.ownerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE52128),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CollageImage(url: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
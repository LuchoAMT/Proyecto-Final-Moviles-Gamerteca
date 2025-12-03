package com.ucb.proyectofinalgamerteca.features.user_library.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            // --- SECCIÓN IZQUIERDA: PORTADAS---
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                if (coverUrls.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Imagen 1
                        if (coverUrls.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(coverUrls[0]).crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 1.dp)
                            )
                        }
                        // Columna para Imagen 2 y 3
                        if (coverUrls.size > 1) {
                            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data(coverUrls[1]).crossfade(true).build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 1.dp)
                                )
                                if (coverUrls.size > 2) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current).data(coverUrls[2]).crossfade(true).build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.weight(1f).fillMaxWidth()
                                    )
                                } else {
                                    Box(modifier = Modifier.weight(1f).background(Color.Black)) // Relleno
                                }
                            }
                        }
                    }
                }
            }

            // --- SECCIÓN DERECHA: INFO ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${list.gameIds.size} elementos",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (list.ownerName.isNotBlank()) list.ownerName else "Usuario desconocido",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}
package com.ucb.proyectofinalgamerteca.features.games.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
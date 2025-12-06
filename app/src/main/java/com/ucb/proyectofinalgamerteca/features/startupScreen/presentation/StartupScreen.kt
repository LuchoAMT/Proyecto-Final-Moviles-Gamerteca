package com.ucb.proyectofinalgamerteca.features.startupScreen.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel
import java.nio.file.Files.size
import kotlin.math.sin


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartupScreen(
    viewModel: StartupViewModel = koinViewModel(),
    onContinue: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = false)

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE74C3C))
    ) {
        WhiteEnergyBackground()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = uiState.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.description,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = uiState.buttonText, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onContinue() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE74C3C), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continuar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WhiteEnergyBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "energy")

    val y1 by infiniteTransition.animateFloat(
        initialValue = 100f, targetValue = 400f, // Movimiento corto en la zona visible
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "y1"
    )

    val y2 by infiniteTransition.animateFloat(
        initialValue = 500f, targetValue = 150f, // Sube y baja en lo alto
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "y2"
    )

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "s1"
    )

    val yRise by infiniteTransition.animateFloat(
        initialValue = 1200f, targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "yRise"
    )

    val xSide by infiniteTransition.animateFloat(
        initialValue = -50f, targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "xSide"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val visibleHeight = size.height * 0.55f

        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = 400f * scale1,
            center = Offset(width * 0.2f, y1)
        )

        // 2. Burbuja Mediana (Derecha)
        drawCircle(
            color = Color.White.copy(alpha = 0.25f),
            radius = 250f,
            center = Offset(width * 0.85f, y2)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.40f), // ¡Bien blanca!
            radius = 180f * scale1,
            center = Offset(width * 0.5f, visibleHeight * 0.4f + y1 * 0.2f)
        )

        val riseRealY = (visibleHeight * 1.2f) + (yRise * 0.5f)

        drawCircle(
            color = Color.White.copy(alpha = 0.30f),
            radius = 120f,
            center = Offset(width * 0.3f + xSide, if(yRise > 0) yRise else -200f)
        )

        val startY = size.height * 0.55f
        val endY = -150f
        val currentY = startY + (endY - startY) * ((yRise - (-200f)) / (2000f - (-200f)))


        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = 80f,
            center = Offset(width * 0.7f, (size.height * 0.5f) - (y1 * 0.5f)) // Flota en el medio
        )
    }
}
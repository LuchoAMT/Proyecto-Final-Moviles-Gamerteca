package com.ucb.proyectofinalgamerteca.features.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinalgamerteca.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    var username by remember(state.username) { mutableStateOf(state.username) }
    var email by remember(state.email) { mutableStateOf(state.email) }
    var phone by remember(state.phone) { mutableStateOf(state.phone) }
    var password by remember(state.password) { mutableStateOf(state.password) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE52128))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FOTO DE PERFIL (ICONO) ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)) // Fondo gris claro
                    .clickable(enabled = isEditing) { /* Lógica futura de cambio */ },
                contentAlignment = Alignment.Center
            ) {
                // ✅ CAMBIO: Usamos Icon en vez de Image
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Imagen de perfil",
                    tint = Color.Gray, // Color del muñeco
                    modifier = Modifier.size(80.dp)
                )
            }

            if (isEditing) {
                TextButton(onClick = { /* Acción futura */ }) {
                    Text("Cambiar Imagen", color = Color(0xFFE52128))
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }

            // --- CAMPOS DE TEXTO ---

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE52128),
                    focusedLabelColor = Color(0xFFE52128),
                    cursorColor = Color(0xFFE52128)
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Email no editable usualmente
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Número de teléfono") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE52128),
                    focusedLabelColor = Color(0xFFE52128),
                    cursorColor = Color(0xFFE52128)
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                placeholder = { Text("********") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE52128),
                    focusedLabelColor = Color(0xFFE52128),
                    cursorColor = Color(0xFFE52128)
                )
            )

            Spacer(Modifier.height(32.dp))

            // --- BOTÓN ACCIÓN ---
            Button(
                onClick = {
                    if (isEditing) {
                        // viewModel.saveProfile(...)
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) Color.Black else Color(0xFFE52128)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (isEditing) "Guardar Cambios" else "Editar Perfil",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
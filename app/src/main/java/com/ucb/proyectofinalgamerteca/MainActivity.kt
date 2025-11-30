package com.ucb.proyectofinalgamerteca

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.navigation.AppNavigation
import com.ucb.proyectofinalgamerteca.navigation.Screen
import com.ucb.proyectofinalgamerteca.ui.theme.ProyectoFinalGamertecaTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM", "Permiso concedido")
        } else {
            Log.d("FCM", "Permiso denegado")
        }
    }

    private fun askNotificationPermission() {
        // Solo es necesario en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Ya tenemos permiso
            } else {
                // Pedimos permiso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        val repo = FirebaseRepository()
        val startDestination = if (repo.getCurrentUserId() != null) {
            Screen.GamesList.route
        } else {
            Screen.Startup.route
        }
        askNotificationPermission()

        setContent {
            ProyectoFinalGamertecaTheme {
                Surface(
                    modifier = Modifier.statusBarsPadding()
                ) {
                    AppNavigation(startDestination = startDestination)
                }
            }
        }
    }
}


package com.ucb.proyectofinalgamerteca

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.ucb.proyectofinalgamerteca.core.presentation.MaintenanceScreen
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.navigation.AppNavigation
import com.ucb.proyectofinalgamerteca.navigation.Screen
import com.ucb.proyectofinalgamerteca.ui.theme.ProyectoFinalGamertecaTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()
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
        // 1. Aseguramos inicialización de Firebase ANTES de tocar nada más
        try {
            FirebaseApp.initializeApp(this)
            Log.d("DEBUG_UI", "✅ Firebase inicializado")
        } catch (e: Exception) {
            Log.e("DEBUG_UI", "❌ Error fatal Firebase: ${e.message}")
        }
        enableEdgeToEdge()

        val repo = FirebaseRepository()
        val startDestination = if (repo.getCurrentUserId() != null) {
            Screen.GamesList.route
        } else {
            Screen.Startup.route
        }
        Log.d("DEBUG_UI", "📍 Destino inicial calculado: $startDestination")
        askNotificationPermission()

        setContent {
            val isMaintenance by mainViewModel.isMaintenance.collectAsState()
            val isLoadingConfig by mainViewModel.isLoadingConfig.collectAsState()
            // LOGS VISUALES: Esto se imprimirá en el Logcat cada vez que cambie el estado
            LaunchedEffect(isLoadingConfig, isMaintenance) {
                Log.d("DEBUG_UI", "🔄 Estado UI -> Loading: $isLoadingConfig | Maintenance: $isMaintenance")
            }
            ProyectoFinalGamertecaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().statusBarsPadding()
                ) {


                    // 2. LÓGICA DE PANTALLA
                    if (isLoadingConfig) {
                        // Pantalla blanca o Splash mientras carga la config remota
                        // Esto evita que se vea la app un milisegundo antes de ponerse roja
                        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                            CircularProgressIndicator(color = Color(0xFFE52128))
                        }
                    } else {
                        if (isMaintenance) {
                            // SI HAY MANTENIMIENTO, MOSTRAMOS ESTO
                            Log.d("DEBUG_UI", "🛑 Renderizando Pantalla Mantenimiento")
                            MaintenanceScreen()
                        } else {
                            // SI NO HAY, MOSTRAMOS TU APP NORMAL
                            Log.d("DEBUG_UI", "✅ Renderizando Navegación Normal")
                            AppNavigation(startDestination = startDestination)
                        }
                    }
                }
            }
        }
    }
}


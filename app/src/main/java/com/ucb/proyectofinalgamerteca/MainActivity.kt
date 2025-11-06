package com.ucb.proyectofinalgamerteca

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.navigation.AppNavigation
import com.ucb.proyectofinalgamerteca.navigation.Screen
import com.ucb.proyectofinalgamerteca.ui.theme.ProyectoFinalGamertecaTheme

class MainActivity : ComponentActivity() {
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


package com.ucb.proyectofinalgamerteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupScreen
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupViewModel
import com.ucb.proyectofinalgamerteca.navigation.AppNavigation
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}


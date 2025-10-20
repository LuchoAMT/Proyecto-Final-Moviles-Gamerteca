package com.ucb.proyectofinalgamerteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ucb.proyectofinalgamerteca.navigation.AppNavigation
import com.ucb.proyectofinalgamerteca.ui.theme.ProyectoFinalGamertecaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoFinalGamertecaTheme {
                AppNavigation()
            }
        }
    }
}


package com.example.enerlex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.enerlex.ui.navigation.AppNavigation
import com.example.enerlex.ui.theme.EnerlexTheme
import com.example.enerlex.ui.theme.ThemeState

/**
 * Punto de entrada de la app EnerFlow.
 * Configura el tema y delega la navegación a AppNavigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnerlexTheme(darkMode = ThemeState.isDarkMode) {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}

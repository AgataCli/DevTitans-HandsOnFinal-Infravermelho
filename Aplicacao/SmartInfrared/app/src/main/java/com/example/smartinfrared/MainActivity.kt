package com.example.smartinfrared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.smartinfrared.navigation.NavGraph
import com.example.smartinfrared.navigation.Routes
import com.example.smartinfrared.ui.theme.SmartInfraredTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartInfraredTheme { // Aplica o tema personalizado
                MainApp() // Inicia o componente principal do app
            }
        }
    }
}

@Composable
private fun MainApp() {
    val navController = rememberNavController() // Controlador de navegação

    NavGraph(
        navController = navController,
        startDestination = Routes.HOME// Tela inicial do app
    )
}
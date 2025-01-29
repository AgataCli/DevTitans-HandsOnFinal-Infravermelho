// NavGraph.kt
package com.example.smartinfrared.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import com.example.smartinfrared.ui.screens.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") { // Rota da tela inicial
            //HomeScreen() // Você precisará criar esta tela depois
        }
        // Adicione outras rotas aqui posteriormente
    }
}
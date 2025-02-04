// NavGraph.kt
package com.example.smartinfrared.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartinfrared.ui.screens.CreateControlScreen
import com.example.smartinfrared.ui.screens.HomeScreen
import com.example.smartinfrared.ui.screens.SavedCommandsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) { // rota da tela inicial
            HomeScreen(navController)
        }

        composable(Routes.COMMANDS) { // rota da tela de comandos salvos
            SavedCommandsScreen(navController)
        }

        composable(Routes.CREATE_CONTROL) { // rota da tela de criar controles
            CreateControlScreen(navController)
        }
    }
}
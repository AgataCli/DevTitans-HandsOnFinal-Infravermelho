package com.example.smartinfrared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController

@Composable
fun SavedCommandsScreen(navController: NavHostController) {
    var menuVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = { menuVisible = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
//            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFAF5454))
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 16.dp)
                ) {
                    Text("Comandos Salvos", color = Color.White, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Adicione, Envie ou Exclua um comando:")
            }

        }

        // Menu lateral animado
        AnimatedVisibility(
            visible = menuVisible,
            enter = slideInHorizontally(animationSpec = tween(300)) { -it },
            exit = slideOutHorizontally(animationSpec = tween(300)) { -it },
            modifier = Modifier.zIndex(1f)
        ) {
            SideMenu(
                navController = navController,
                onCloseMenu = { menuVisible = false }
            )
        }
    }
}
package com.example.smartinfrared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import com.example.smartinfrared.navigation.Routes

@Composable
fun HomeScreen(navController: NavHostController) {
    var menuVisible by remember { mutableStateOf(false) }
    val recentControls = listOf("TV Sala", "Ar Condicionado", "Home Theater", "Luz Quarto")

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
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Botoes(navController)

                Spacer(modifier = Modifier.height(16.dp))

                RecentControlsList(recentControls)
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

@Composable
fun Botoes(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        //.background(Color(0xFF7ddb46)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color(0xFFAF5454)),
                onClick = {
                    try {
                        navController.navigate(Routes.COMMANDS) {
                            popUpTo(Routes.COMMANDS) {
                                saveState = true
                            }
                            var lauchSingleTop = true
                        }
                    } catch (e: Exception) {
                        println("Erro na navegacao: ${e.message}")
                    }
                },
            ) {
                Text(
                    text = "Comandos Salvos",
                    fontSize = 25.sp,
                    modifier = Modifier.padding(25.dp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color(0xFFAF5454)),
                onClick = {
                    try {
                        navController.navigate(Routes.CREATE_CONTROL) {
                            popUpTo(Routes.CREATE_CONTROL) {
                                saveState = true
                            }
                            var lauchSingleTop = true
                        }
                    } catch (e: Exception) {
                        println("Erro na navegacao: ${e.message}")
                    }
                },
            ) {
                Text(
                    text = "Crie seu Controle",
                    fontSize = 25.sp,
                    modifier = Modifier.padding(25.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RecentControlsList(controls: List<String>) {
    Text(
        text = "Controles Recentes:",
        fontSize = 25.sp,
        modifier = Modifier.padding(8.dp),
        //color = Color.Black
    )
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(controls) { control ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { /* Ação ao clicar no controle */ }
                    .background(Color(0xFFEDE7F6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = control,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black
                )
            }
        }
    }
}
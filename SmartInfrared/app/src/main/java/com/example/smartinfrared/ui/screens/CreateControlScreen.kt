package com.example.smartinfrared.ui.screens

import android.widget.Button
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
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
import android.widget.CheckBox

@Composable
fun CreateControlScreen(navController: NavHostController) {
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabeçalho
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFAF5454))
                        .padding(16.dp)
                ) {
                    Text("Crie o seu Controle", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Selecione os comandos salvos que quer adicionar ao seu controle", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))
                // Chama a lista dos Controles Salvos
                ListaComandos()
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

// LISTA DE CONTROLES SALVOS
@Composable
fun ListaComandos() {
    val listaComandos: List<String> = mutableListOf("Ligar TV", "Aumentar Volume", "Abaixar Volume")
//    val checkBox : CheckBox
//    val button: Button
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        //.background(Color(0xFF7ddb46)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {

            listaComandos.forEach{
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(8.dp)
                        .background(Color(0xFFEDE7F6))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(it, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.Black)
//                    texto = findViewById
                }
            }
        }
    }
}
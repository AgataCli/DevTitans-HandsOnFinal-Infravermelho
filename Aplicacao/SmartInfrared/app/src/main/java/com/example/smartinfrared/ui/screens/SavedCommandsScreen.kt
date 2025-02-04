package com.example.smartinfrared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.smartinfrared.data.database.CommandEntity
import com.example.smartinfrared.data.repository.CommandRepository
import com.example.smartinfrared.ui.viewmodel.SavedCommandsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun SavedCommandsScreen(navController: NavHostController) {
    val viewModel: SavedCommandsViewModel = hiltViewModel()
    val commands by viewModel.commands.collectAsState(initial = emptyList())
    var menuVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var newCommand by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = { menuVisible = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF673AB7)
            ) {
                Text("+", color = Color.White, fontSize = 24.sp)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
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
                    Text("Comandos Salvos", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Adicione, Envie ou Exclua um comando:", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de comandos
                commands.forEach { command ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(8.dp)
                            .background(Color(0xFFEDE7F6))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(command.name, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.Black)

                        IconButton(onClick = { /* Enviar comando */ }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Enviar",
                                tint = Color(0xFF673AB7)
                            )
                        }

                        IconButton(onClick = { viewModel.deleteCommand(command) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Excluir",
                                tint = Color(0xFF673AB7)
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Novo Comando") },
                text = {
                    Column {
                        Text("Digite o nome do comando:")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = newCommand,
                            onValueChange = { newCommand = it },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newCommand.text.isNotBlank()) {
                                viewModel.insertCommand(
                                    CommandEntity(
                                        name = newCommand.text,
                                        signalPattern = "" // Adicione o padrão de sinal aqui
                                    )
                                )
                                newCommand = TextFieldValue("")
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

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

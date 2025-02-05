package com.example.smartinfrared.ui.screens

import android.content.Context.CONSUMER_IR_SERVICE
import android.hardware.ConsumerIrManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartinfrared.data.database.CommandEntity
import com.example.smartinfrared.ui.viewmodel.SavedCommandsViewModel

@Composable
fun SavedCommandsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: SavedCommandsViewModel = hiltViewModel()
    val commands by viewModel.commands.collectAsState(initial = emptyList())
    var menuVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var newCommand by remember { mutableStateOf(TextFieldValue("")) }
    var newSignal by remember { mutableStateOf(emptyList<Int>()) }

    Scaffold(
        topBar = { AppTopBar(onMenuClick = { menuVisible = true }) },
        floatingActionButton = { AddCommandButton { showDialog = true } }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            CommandList(commands, viewModel, context)
        }

        if (showDialog) {
            newSignal = listOf(38, 2, 3, 4, 5)
            AddCommandDialog(
                newCommand = newCommand,
                newSignal = newSignal,
                onNewCommandChange = { newCommand = it },
                onSave = {
                    if (newCommand.text.isNotBlank()) {
                        viewModel.insertCommand(
                            CommandEntity(
                                name = newCommand.text,
                                signalPattern = newSignal
                            )
                        )

                        Toast.makeText(
                            context,
                            "Salvando o sinal ${newSignal.joinToString(", ")}",
                            Toast.LENGTH_SHORT
                        ).show()

                        newCommand = TextFieldValue("")
                        newSignal = emptyList()
                        showDialog = false
                    }
                },
                onDismiss = { showDialog = false }
            )
        }

        SideMenu(menuVisible, navController) { menuVisible = false }
    }
}

@Composable
fun AddCommandButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF673AB7)
    ) {
        Text("+", color = Color.White, fontSize = 24.sp)
    }
}

@Composable
fun CommandList(
    commands: List<CommandEntity>,
    viewModel: SavedCommandsViewModel,
    context: android.content.Context
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Adicione, Envie ou Exclua um comando:", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(commands) { command ->
                CommandItem(command, viewModel, context)
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFAF5454))
            .padding(16.dp)
    ) {
        Text("Comandos Salvos", color = Color.White, fontSize = 20.sp)
    }
}

@Composable
fun CommandItem(
    command: CommandEntity,
    viewModel: SavedCommandsViewModel,
    context: android.content.Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(8.dp)
            .background(Color(0xFFEDE7F6))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(command.name, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.Black)
        SendCommandButton(command, context)
        DeleteCommandButton(command, viewModel)
    }
}

@Composable
fun SendCommandButton(command: CommandEntity, context: android.content.Context) {
    val frequency = command.signalPattern[0]
    val pattern = command.signalPattern.subList(1, command.signalPattern.size)
    IconButton(onClick = {
        val consumerIrManager = context.getSystemService(ConsumerIrManager::class.java) as ConsumerIrManager?

        if (consumerIrManager == null) {
            println("ConsumerIrManager não disponível")
            Toast.makeText(context, "Dispositivo não suporta IR!", Toast.LENGTH_SHORT).show()
            return@IconButton
        }

        if (!consumerIrManager.hasIrEmitter()) {
            println("Emissor IR não encontrado")
            Toast.makeText(context, "Emissor IR não encontrado!", Toast.LENGTH_SHORT).show()
            return@IconButton
        }

        try {
            val frequency = command.signalPattern[0]
            val pattern = command.signalPattern.subList(1, command.signalPattern.size)
            consumerIrManager.transmit(frequency * 1000, pattern.toIntArray())
            Toast.makeText(context, "Sinal enviado: ${pattern.joinToString(", ")}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            println("Erro ao enviar sinal: ${e.message}")
            Toast.makeText(context, "Erro ao enviar sinal!", Toast.LENGTH_SHORT).show()
        }
    }) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Enviar",
            tint = Color(0xFF673AB7)
        )
    }
}

@Composable
fun DeleteCommandButton(command: CommandEntity, viewModel: SavedCommandsViewModel) {
    IconButton(onClick = { viewModel.deleteCommand(command) }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Excluir",
            tint = Color(0xFF673AB7)
        )
    }
}

@Composable
fun AddCommandDialog(
    newCommand: TextFieldValue,
    newSignal: List<Int>,
    onNewCommandChange: (TextFieldValue) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Comando") },
        text = {
            Column {
                Text("Digite o nome do comando:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newCommand,
                    onValueChange = onNewCommandChange,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Recebendo sinal:")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (newSignal.isEmpty()) "Nenhum sinal recebido"
                    else {newSignal.joinToString(", ")},
                    color = Color.White
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave()
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SideMenu(
    visible: Boolean,
    navController: NavHostController,
    onCloseMenu: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(animationSpec = tween(300)) { -it },
        exit = slideOutHorizontally(animationSpec = tween(300)) { -it },
        modifier = Modifier.zIndex(1f)
    ) {
        SideMenu(
            navController = navController,
            onCloseMenu = onCloseMenu
        )
    }
}
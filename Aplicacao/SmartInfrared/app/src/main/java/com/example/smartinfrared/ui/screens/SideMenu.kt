// SideMenu.kt
package com.example.smartinfrared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.smartinfrared.navigation.Routes

@Composable
fun SideMenu(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onCloseMenu: () -> Unit
) {
    Box(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Cabeçalho do menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Fechar menu",
                    modifier = Modifier
                        .clickable { onCloseMenu() }
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            //Divider()

            // Itens do menu
            MenuItem(
                text = "Menu Inicial",
                onClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(navController.graph.startDestinationId)
                        var lauchSingleTop = true
                    }
                    onCloseMenu()
                }
            )

            MenuItem(
                "Comandos",
                onClick = {
                    try {
                        navController.navigate(Routes.COMMANDS) {
                            popUpTo(Routes.COMMANDS) {
                                saveState = true
                            }
                            var lauchSingleTop = true
                        }
                        onCloseMenu()
                    } catch (e: Exception) {
                        println("Erro na navegacao: ${e.message}")
                    }
                }
            )


            MenuItem(
                "Crie o Seu",
                onClick = {
                    try {
                        navController.navigate(Routes.CREATE_CONTROL) {
                            popUpTo(Routes.CREATE_CONTROL) {
                                saveState = true
                            }
                            var launchSingleTop = true
                        }
                        onCloseMenu()
                    } catch (e: Exception) {
                        println("Erro na navegacao: ${e.message}")
                    }
                }
            )
//
//
//            MenuItem(
//                "Controles",
//                onClick = TODO()
//            )


            val shouldShowDialog = remember { mutableStateOf(false) }
            if (shouldShowDialog.value) {
                SobreAlertDialog(shouldShowDialog = shouldShowDialog)
            }
            MenuItem(
                "Saiba Mais",
                onClick = { shouldShowDialog.value = true  }
            )

        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clickable {
                android.util.Log.d("NAV_DEBUG", "Item clicado: $text")
                onClick()
            }
            .padding(vertical = 12.dp)
    )
}


@Composable
fun SobreAlertDialog(shouldShowDialog: MutableState<Boolean>) {
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            title = { Text(text = "Sobre o SmartInfrared") },
            text = {
                Text(text = "Projeto final do DevTitans.\n" +
                        "Desenvolvido por:\n" +
                        "Ágata Brasão\n" +
                        "Bianka Maciel\n" +
                        "Juan Veiga\n" +
                        "Maria Fernanda Cabral\n" +
                        "Mateus Pantoja\n" +
                        "José Inácio\n")
            },

            confirmButton = {
                Button(
                    onClick = { shouldShowDialog.value = false }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }
}

// TopAppBar com botão do menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = { Text("SmartInfrared") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu"
                )
            }
        }
    )
}
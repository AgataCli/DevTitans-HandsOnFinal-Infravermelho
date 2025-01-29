package com.example.smartinfrared.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartinfrared.ui.theme.SmartInfraredTheme

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideMenu(
    drawerState: DrawerState?,
    onItemSelected: (() -> Unit)? = {},
    onClose: (() -> Unit)? = {}
) {
    // Itens do menu
    val menuItems = listOf(
        MenuItem(
            title = "Menu Inicial",
            icon = Icons.Default.Home
        ),
        MenuItem(
            title = "Crie o Seu",
            icon = Icons.Default.Create
        ),
        MenuItem(
            title = "Comandos",
            icon = Icons.Default.List
        ),
        MenuItem(
            title = "Controles",
            icon = Icons.Default.Settings
        ),
        MenuItem(
            title = "Saiba Mais",
            icon = Icons.Default.Info
        )
    )

    ModalDrawerSheet(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cabeçalho do menu
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Smart Infrared",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Controle seus dispositivos",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Divider()

        // Itens de navegação
        Column(modifier = Modifier.padding(8.dp)) {
            menuItems.forEach { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(text = item.title)
                    },
                    selected = item.selected,
                    onClick = {
                        onItemSelected?.let { it() }
                        onClose?.let { it() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SideMenuPreview() {
    SmartInfraredTheme {
        SideMenu(
            drawerState = null,
            onItemSelected = null,
            onClose = null
        )
    }
}
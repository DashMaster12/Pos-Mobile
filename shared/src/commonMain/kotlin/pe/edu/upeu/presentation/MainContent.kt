package pe.edu.upeu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainContent(onLogout: () -> Unit) {
    var currentRoute by remember { mutableStateOf<PosRoute>(PosRoute.POS) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Menú Lateral (Navigation Rail)
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text(
                "POS QSR",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val items = listOf(PosRoute.Dashboard, PosRoute.POS, PosRoute.Ordenes, PosRoute.Caja)
            items.forEach { screen ->
                NavigationRailItem(
                    selected = currentRoute == screen,
                    onClick = { currentRoute = screen },
                    // Usando iconos genéricos que ya tienes importados
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text(screen.title) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NavigationRailItem(
                selected = false,
                onClick = onLogout,
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Salir") },
                label = { Text("Salir") }
            )
        }

        // Área de contenido a la derecha
        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentRoute) {
                is PosRoute.Dashboard -> DashboardContent()
                is PosRoute.POS -> PosContent()
                is PosRoute.Ordenes -> OrdenesContent()
                is PosRoute.Caja -> CajaContent()
            }
        }
    }
}
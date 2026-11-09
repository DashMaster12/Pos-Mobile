package pe.edu.upeu.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen {
    object Splash : Screen()
    object Welcome : Screen()
    object Main : Screen() // Contenedor principal de la app de delivery
}

// Rutas del cajón del POS de staff. La app de cliente usa DeliveryRoute.
sealed class PosRoute(val title: String) {
    object Dashboard : PosRoute("Dashboard")
    object POS : PosRoute("POS")
    object Ordenes : PosRoute("Órdenes")
    object Caja : PosRoute("Caja")
}

// Rutas de navegación inferior (Bottom Navigation) para la app de delivery
sealed class DeliveryRoute(val route: String, val title: String, val icon: ImageVector) {
    object Tienda : DeliveryRoute("tienda", "Tienda", Icons.Default.Home)
    object MisPedidos : DeliveryRoute("mis_pedidos", "Mis Pedidos", Icons.Default.List)
    object Configuracion : DeliveryRoute("configuracion", "Configuración", Icons.Default.Settings)
    object Usuario : DeliveryRoute("usuario", "Usuario", Icons.Default.Person)
}

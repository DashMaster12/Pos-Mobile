package pe.edu.upeu.presentation

sealed class Screen {
    object Splash : Screen()
    object Welcome : Screen()
    object Main : Screen() // Este será el contenedor del menú lateral
}

// Rutas internas para el menú lateral del POS
sealed class PosRoute(val route: String, val title: String) {
    object Dashboard : PosRoute("dashboard", "Dashboard")
    object POS : PosRoute("pos", "Punto de venta")
    object Ordenes : PosRoute("ordenes", "Órdenes")
    object Caja : PosRoute("caja", "Caja")
}
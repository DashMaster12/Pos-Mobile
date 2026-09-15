package pe.edu.upeu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.presentation.screens.CartScreen
import pe.edu.upeu.presentation.screens.ConfiguracionScreen
import pe.edu.upeu.presentation.screens.MisPedidosScreen
import pe.edu.upeu.presentation.screens.OrderModel
import pe.edu.upeu.presentation.screens.ProductItem
import pe.edu.upeu.presentation.screens.TiendaScreen
import pe.edu.upeu.presentation.screens.UsuarioScreen

data class CartItem(
    val product: ProductItem,
    var quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainContent(onLogout: () -> Unit) {
    var currentRoute by remember { mutableStateOf<DeliveryRoute>(DeliveryRoute.Tienda) }

    // Estado global del carrito y pantalla de carrito completa
    val cartItems = remember { mutableStateListOf<CartItem>() }
    var showCartScreen by remember { mutableStateOf(false) }

    // Direcciones disponibles y dirección seleccionada
    val addresses = remember { mutableStateListOf("Av. Principal 123, Lima", "Calle Los Pinos 456, Miraflores") }
    var selectedAddress by remember { mutableStateOf(addresses[0]) }

    // Estado global de pedidos (inicializado con ejemplos)
    val ordersList = remember {
        mutableStateListOf(
            OrderModel("#1089", "Pedido #1089 (Hamburguesa Deluxe)", "En Camino 🛵", "$25.00", "1x Hamburguesa Deluxe - Dir: Av. Principal 123"),
            OrderModel("#1072", "Pedido #1072 (Pizza Pepperoni)", "Entregado ✅", "$45.00", "1x Pizza Pepperoni - Dir: Calle Los Pinos 456")
        )
    }

    val totalCartCount = cartItems.sumOf { it.quantity }

    val items = listOf(
        DeliveryRoute.Tienda,
        DeliveryRoute.MisPedidos,
        DeliveryRoute.Configuracion,
        DeliveryRoute.Usuario
    )

    if (showCartScreen) {
        // Pantalla Completa de Carrito de Compras
        CartScreen(
            cartItems = cartItems,
            onUpdateQuantity = { item, newQty ->
                item.quantity = newQty
            },
            onRemoveItem = { item ->
                cartItems.remove(item)
            },
            addresses = addresses,
            selectedAddress = selectedAddress,
            onSelectAddress = { addr ->
                selectedAddress = addr
            },
            onAddNewAddress = { newAddr ->
                addresses.add(newAddr)
                selectedAddress = newAddr
            },
            onBackClick = {
                showCartScreen = false
            },
            onConfirmOrder = {
                val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
                val newOrderId = "#${1100 + ordersList.size}"
                val detailsText = cartItems.joinToString(", ") { "${it.quantity}x ${it.product.name}" } + " - Dir: $selectedAddress"
                
                val newOrder = OrderModel(
                    id = newOrderId,
                    title = "Pedido $newOrderId",
                    status = "En preparación 🛵",
                    totalStr = "$${totalPrice}0",
                    details = detailsText
                )
                ordersList.add(0, newOrder)

                // Limpiar carrito y cerrar pantalla de carrito
                cartItems.clear()
                showCartScreen = false

                // Redirigir a Mis Pedidos
                currentRoute = DeliveryRoute.MisPedidos
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentRoute.title, style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        if (currentRoute is DeliveryRoute.Tienda) {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge { Text("$totalCartCount") }
                                    }
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                IconButton(onClick = { showCartScreen = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Carrito",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen,
                            onClick = { currentRoute = screen },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                when (currentRoute) {
                    is DeliveryRoute.Tienda -> TiendaScreen(
                        onAddToCart = { product ->
                            val existing = cartItems.find { it.product.id == product.id }
                            if (existing != null) {
                                existing.quantity++
                            } else {
                                cartItems.add(CartItem(product, 1))
                            }
                        }
                    )
                    is DeliveryRoute.MisPedidos -> MisPedidosScreen(ordersList = ordersList)
                    is DeliveryRoute.Configuracion -> ConfiguracionScreen()
                    is DeliveryRoute.Usuario -> UsuarioScreen(onLogout = onLogout)
                }
            }
        }
    }
}

package pe.edu.upeu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

    // Estado global de sesión compartido (por defecto true)
    var isLoggedIn by remember { mutableStateOf(true) }

    // Estado global del carrito y pantalla de carrito completa
    val cartItems = remember { mutableStateListOf<CartItem>() }
    var showCartScreen by remember { mutableStateOf(false) }

    // Diálogo de aviso cuando usuario invitado intenta agregar al carrito
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    // Direcciones disponibles y dirección seleccionada
    val addresses = remember { mutableStateListOf("Av. Principal 123, Lima", "Calle Los Pinos 456, Miraflores") }
    var selectedAddress by remember { mutableStateOf(addresses[0]) }

    // Estado global de pedidos (inicializado con ejemplos detallados)
    val ordersList = remember {
        mutableStateListOf(
            OrderModel(
                id = "#1089",
                title = "Pedido #1089",
                status = "En Camino 🛵",
                totalStr = "$30.00",
                details = "1x Hamburguesa Deluxe",
                date = "Hoy, 11:15 AM",
                address = "Av. Principal 123, Lima",
                deliveryFee = "$5.00",
                itemsSummary = listOf("1x Hamburguesa Deluxe ($25.00)")
            ),
            OrderModel(
                id = "#1072",
                title = "Pedido #1072",
                status = "Entregado ✅",
                totalStr = "$50.00",
                details = "1x Pizza Pepperoni",
                date = "Ayer, 08:30 PM",
                address = "Calle Los Pinos 456, Miraflores",
                deliveryFee = "$5.00",
                itemsSummary = listOf("1x Pizza Pepperoni ($45.00)")
            )
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
            isLoggedIn = isLoggedIn,
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
                val subtotal = cartItems.sumOf { it.product.price * it.quantity }
                val deliveryFeeValue = 5.0
                val totalWithDelivery = subtotal + deliveryFeeValue
                
                val newOrderId = "#${1100 + ordersList.size}"
                val itemsSummaryList = cartItems.map { "${it.quantity}x ${it.product.name} (${it.product.priceStr})" }
                val detailsText = cartItems.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
                
                val newOrder = OrderModel(
                    id = newOrderId,
                    title = "Pedido $newOrderId",
                    status = "En preparación 🛵",
                    totalStr = "$${totalWithDelivery}0",
                    details = detailsText,
                    date = "Hoy, Recién realizado",
                    address = selectedAddress,
                    deliveryFee = "$${deliveryFeeValue}0",
                    itemsSummary = itemsSummaryList
                )
                ordersList.add(0, newOrder)

                // Limpiar carrito y cerrar pantalla de carrito
                cartItems.clear()
                showCartScreen = false

                // Redirigir a Mis Pedidos
                currentRoute = DeliveryRoute.MisPedidos
            },
            onGoToLogin = {
                showCartScreen = false
                currentRoute = DeliveryRoute.Usuario
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            currentRoute.title, 
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        if (currentRoute is DeliveryRoute.Tienda) {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        ) { 
                                            Text("$totalCartCount") 
                                        }
                                    }
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                IconButton(onClick = { showCartScreen = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Carrito",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
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
                        isLoggedIn = isLoggedIn,
                        onAddToCart = { product ->
                            val existing = cartItems.find { it.product.id == product.id }
                            if (existing != null) {
                                existing.quantity++
                            } else {
                                cartItems.add(CartItem(product, 1))
                            }
                        },
                        onLoginRequired = {
                            showLoginRequiredDialog = true
                        }
                    )
                    is DeliveryRoute.MisPedidos -> MisPedidosScreen(
                        isLoggedIn = isLoggedIn,
                        ordersList = ordersList,
                        onGoToLogin = {
                            currentRoute = DeliveryRoute.Usuario
                        }
                    )
                    is DeliveryRoute.Configuracion -> ConfiguracionScreen()
                    is DeliveryRoute.Usuario -> UsuarioScreen(
                        isLoggedInExternal = isLoggedIn,
                        onSessionStateChanged = { newStatus ->
                            isLoggedIn = newStatus
                        },
                        onLogout = onLogout
                    )
                }
            }
        }
    }

    // Diálogo de aviso cuando usuario invitado intenta agregar productos
    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showLoginRequiredDialog = false },
            title = { Text("Inicio de sesión requerido", fontWeight = FontWeight.Bold) },
            text = { Text("Debes iniciar sesión para agregar productos a tu carrito de compras.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginRequiredDialog = false
                        currentRoute = DeliveryRoute.Usuario
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ir a Iniciar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginRequiredDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

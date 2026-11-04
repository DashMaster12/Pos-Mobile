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
import androidx.compose.ui.window.Dialog
import pe.edu.upeu.data.model.ClienteResponse
import pe.edu.upeu.data.model.ProductDto
import pe.edu.upeu.presentation.screens.CartScreen
import pe.edu.upeu.presentation.screens.ConfiguracionScreen
import pe.edu.upeu.presentation.screens.MisPedidosScreen
import pe.edu.upeu.presentation.screens.TiendaScreen
import pe.edu.upeu.presentation.screens.UsuarioScreen
import pe.edu.upeu.presentation.viewmodel.AppViewModel

class CartItem(
    val product: ProductDto,
    initialQuantity: Int
) {
    var quantity by mutableIntStateOf(initialQuantity)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainContent(
    onLogout: () -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val viewModel = remember { AppViewModel() }
    val products by viewModel.products.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val historyOrders by viewModel.historyOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentRoute by remember { mutableStateOf<DeliveryRoute>(DeliveryRoute.Tienda) }

    // Estado global de sesión compartido (inicia en false)
    var isLoggedIn by remember { mutableStateOf(false) }
    var loggedInCliente by remember { mutableStateOf<ClienteResponse?>(null) }

    // Estado global del carrito y pantallas
    val cartItems = viewModel.cartItems

    var showCartScreen by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var registrationError by remember { mutableStateOf<String?>(null) }
    var orderError by remember { mutableStateOf<String?>(null) }
    var showOrderErrorDialog by remember { mutableStateOf(false) }

    val addresses = remember { mutableStateListOf<String>() }
    var selectedAddress by remember { mutableStateOf("") }

    val totalCartCount = cartItems.size

    val items = listOf(
        DeliveryRoute.Tienda,
        DeliveryRoute.MisPedidos,
        DeliveryRoute.Configuracion,
        DeliveryRoute.Usuario
    )

    // Cargar órdenes cuando haya inicio de sesión activo
    LaunchedEffect(isLoggedIn, loggedInCliente?.id) {
        if (isLoggedIn) {
            loggedInCliente?.id?.let { cId ->
                viewModel.fetchOrdersForCliente(cId)
            }
        }
    }

    if (showCartScreen) {
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
                if (!addresses.contains(newAddr)) {
                    addresses.add(newAddr)
                }
                selectedAddress = newAddr
            },
            onBackClick = {
                showCartScreen = false
            },
            onConfirmOrder = {
                val shippingAddr = if (selectedAddress.isNotBlank()) selectedAddress else "Dirección principal"
                viewModel.confirmOrder(
                    cliente = loggedInCliente,
                    address = shippingAddr,
                    onSuccess = { _ ->
                        showCartScreen = false
                        currentRoute = DeliveryRoute.MisPedidos
                    },
                    onError = { err ->
                        orderError = err
                        showOrderErrorDialog = true
                    }
                )
            },
            onGoToLogin = {
                showCartScreen = false
                showLoginDialog = true
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
                            onClick = { 
                                currentRoute = screen 
                                if (screen is DeliveryRoute.MisPedidos && isLoggedIn) {
                                    loggedInCliente?.id?.let { cId ->
                                        viewModel.fetchOrdersForCliente(cId)
                                    }
                                }
                            },
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
                        products = products,
                        activeOrders = activeOrders,
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
                        },
                        onGoToActiveOrders = {
                            currentRoute = DeliveryRoute.MisPedidos
                        }
                    )
                    is DeliveryRoute.MisPedidos -> MisPedidosScreen(
                        isLoggedIn = isLoggedIn,
                        activeOrders = activeOrders,
                        historyOrders = historyOrders,
                        onRefresh = {
                            loggedInCliente?.id?.let { cId ->
                                viewModel.fetchOrdersForCliente(cId)
                            }
                        },
                        onGoToLogin = {
                            showLoginDialog = true
                        }
                    )
                    is DeliveryRoute.Configuracion -> ConfiguracionScreen(
                        darkModeEnabled = darkModeEnabled,
                        onDarkModeChange = onDarkModeChange
                    )
                    is DeliveryRoute.Usuario -> UsuarioScreen(
                        isLoggedInExternal = isLoggedIn,
                        cliente = loggedInCliente,
                        addresses = addresses,
                        onSessionStateChanged = { newStatus ->
                            if (newStatus) {
                                showLoginDialog = true
                            } else {
                                isLoggedIn = false
                                loggedInCliente = null
                                addresses.clear()
                            }
                        },
                        onRegisterClick = {
                            showRegisterDialog = true
                        },
                        onUpdateAddress = { newAddr ->
                            val currentClient = loggedInCliente
                            if (currentClient != null) {
                                viewModel.updateClienteAddress(
                                    cliente = currentClient,
                                    newAddress = newAddr,
                                    onSuccess = { updated ->
                                        loggedInCliente = updated
                                        if (!addresses.contains(newAddr)) {
                                            addresses.add(newAddr)
                                        }
                                        selectedAddress = newAddr
                                    },
                                    onError = { _ -> }
                                )
                            }
                        },
                        onLogout = {
                            isLoggedIn = false
                            loggedInCliente = null
                            addresses.clear()
                            onLogout()
                        }
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
                        showLoginDialog = true
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

    // Diálogo de error al crear el pedido (ej. caja cerrada)
    if (showOrderErrorDialog) {
        AlertDialog(
            onDismissRequest = { showOrderErrorDialog = false },
            title = { Text("No se pudo procesar el pedido", fontWeight = FontWeight.Bold) },
            text = { Text(orderError ?: "Ocurrió un error al registrar tu pedido en el sistema.") },
            confirmButton = {
                Button(
                    onClick = { showOrderErrorDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // Diálogo / Formulario de Inicio de Sesión Real con /api/clientes/login
    if (showLoginDialog) {
        Dialog(onDismissRequest = { showLoginDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                LoginSheetContent(
                    onLoginClick = { identifier, password ->
                        loginError = null
                        viewModel.loginCliente(
                            identifier = identifier,
                            password = password,
                            onSuccess = { cliente ->
                                addresses.clear()
                                if (!cliente.direccion.isNullOrBlank()) {
                                    addresses.add(cliente.direccion)
                                    selectedAddress = cliente.direccion
                                }
                                loggedInCliente = cliente
                                isLoggedIn = true
                                showLoginDialog = false
                                currentRoute = DeliveryRoute.Tienda
                            },
                            onError = { err ->
                                loginError = err
                            }
                        )
                    },
                    onGoToRegister = {
                        showLoginDialog = false
                        showRegisterDialog = true
                    },
                    isLoading = isLoading,
                    errorMessage = loginError
                )
            }
        }
    }

    // Diálogo / Sheet de Registro de Cliente
    if (showRegisterDialog) {
        Dialog(onDismissRequest = { showRegisterDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                RegisterSheetContent(
                    onRegisterClick = { nombreCompleto, tipoDocumento, numeroDocumento, direccion, correo, password ->
                        registrationError = null
                        viewModel.registerCliente(
                            nombreCompleto = nombreCompleto,
                            tipoDocumento = tipoDocumento,
                            numeroDocumento = numeroDocumento,
                            direccion = direccion,
                            correo = correo,
                            password = password,
                            onSuccess = { cliente ->
                                addresses.clear()
                                if (!cliente.direccion.isNullOrBlank()) {
                                    addresses.add(cliente.direccion)
                                    selectedAddress = cliente.direccion
                                }
                                loggedInCliente = cliente
                                isLoggedIn = true
                                showRegisterDialog = false
                                currentRoute = DeliveryRoute.Tienda
                            },
                            onError = { err ->
                                registrationError = err
                            }
                        )
                    },
                    onGoToLogin = {
                        showRegisterDialog = false
                        showLoginDialog = true
                    },
                    isLoading = isLoading,
                    errorMessage = registrationError
                )
            }
        }
    }
}

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
import kotlinx.coroutines.launch
import pe.edu.upeu.data.model.ClienteResponse
import pe.edu.upeu.data.model.DireccionDto
import pe.edu.upeu.data.model.ProductDto
import pe.edu.upeu.data.network.ApiService
import pe.edu.upeu.presentation.screens.CartScreen
import pe.edu.upeu.presentation.screens.CheckoutScreen
import pe.edu.upeu.presentation.screens.ConfiguracionScreen
import pe.edu.upeu.presentation.screens.MisPedidosScreen
import pe.edu.upeu.presentation.screens.ProductCustomizationScreen
import pe.edu.upeu.presentation.screens.TiendaScreen
import pe.edu.upeu.presentation.screens.OrderModel
import pe.edu.upeu.presentation.screens.UsuarioScreen
import pe.edu.upeu.presentation.viewmodel.AppViewModel
import pe.edu.upeu.showOrderNotification

class CartItem(
    val product: ProductDto,
    val unitPrice: Double,
    val customizationNotes: String,
    initialQuantity: Int
) {
    var quantity by mutableIntStateOf(initialQuantity)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainContent(
    cliente: ClienteResponse,
    onLogout: () -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val viewModel = remember { AppViewModel() }
    val apiService = remember { ApiService() }
    val scope = rememberCoroutineScope()

    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val historyOrders by viewModel.historyOrders.collectAsState()

    var currentRoute by remember { mutableStateOf<DeliveryRoute>(DeliveryRoute.Tienda) }

    // Estado global del carrito y pantallas
    val cartItems = viewModel.cartItems

    var showCartScreen by remember { mutableStateOf(false) }
    var showCheckoutScreen by remember { mutableStateOf(false) }
    var checkoutError by remember { mutableStateOf<String?>(null) }
    var customizingProduct by remember { mutableStateOf<ProductDto?>(null) }
    var orderError by remember { mutableStateOf<String?>(null) }
    var showOrderErrorDialog by remember { mutableStateOf(false) }
    var paidOrder by remember { mutableStateOf<OrderModel?>(null) }

    val addresses = remember { mutableStateListOf<String>() }
    val direccionesState = remember { mutableStateListOf<DireccionDto>() }
    var selectedAddress by remember { mutableStateOf("") }

    // Función para cargar direcciones 100% dinámicas desde el backend
    fun loadDirecciones() {
        cliente.id?.let { cId ->
            scope.launch {
                val list = try {
                    apiService.getDirecciones(cId)
                } catch (e: Exception) {
                    emptyList()
                }
                
                direccionesState.clear()
                direccionesState.addAll(list)
                
                addresses.clear()
                list.forEach { d ->
                    addresses.add(d.direccion)
                }
                if (selectedAddress.isNotBlank() && addresses.none { it == selectedAddress }) {
                    addresses.add(selectedAddress)
                }

                if (selectedAddress.isBlank() && addresses.isNotEmpty()) {
                    selectedAddress = list.firstOrNull { it.esPrincipal == true }?.direccion ?: addresses[0]
                }
            }
        }
    }

    // Inicializar direcciones y órdenes del cliente
    LaunchedEffect(cliente.id) {
        loadDirecciones()
        cliente.id?.let { cId ->
            viewModel.watchOrders(cId)
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.stopWatchingOrders() }
    }

    val totalCartCount = cartItems.size

    val items = listOf(
        DeliveryRoute.Tienda,
        DeliveryRoute.MisPedidos,
        DeliveryRoute.Configuracion,
        DeliveryRoute.Usuario
    )

    if (customizingProduct != null) {
        ProductCustomizationScreen(
            product = customizingProduct!!,
            allProducts = products,
            onBackClick = { customizingProduct = null },
            onAddToCart = { customPrice, notes ->
                cartItems.add(CartItem(customizingProduct!!, customPrice, notes, 1))
                customizingProduct = null
            }
        )
    } else if (showCheckoutScreen) {
        CheckoutScreen(
            cartItems = cartItems,
            addresses = addresses,
            selectedAddress = selectedAddress,
            onSelectAddress = { selectedAddress = it },
            onAddAddress = { newAddr ->
                if (!addresses.contains(newAddr)) {
                    addresses.add(newAddr)
                }
                selectedAddress = newAddr
                scope.launch {
                    cliente.id?.let { cId ->
                        try {
                            apiService.agregarDireccion(
                                cId,
                                DireccionDto(direccion = newAddr, esPrincipal = addresses.size == 1)
                            )
                            loadDirecciones()
                        } catch (e: Exception) {
                            checkoutError = null
                        }
                    }
                }
            },
            isLoading = isLoading,
            errorMessage = checkoutError,
            onBack = {
                checkoutError = null
                showCheckoutScreen = false
            },
            onPayYape = { phone, otp, amount ->
                checkoutError = null
                val email = cliente.correo?.takeIf { it.isNotBlank() } ?: "test_payer@example.com"
                val shippingAddr = selectedAddress.ifBlank { addresses.firstOrNull() ?: "Dirección principal" }
                viewModel.payWithYape(
                    phone = phone,
                    otp = otp,
                    amount = amount,
                    email = email,
                    onSuccess = {
                        viewModel.confirmOrder(
                            cliente = cliente,
                            address = shippingAddr,
                            onSuccess = { order -> paidOrder = order },
                            onError = { err ->
                                checkoutError = "El pago se aprobó, pero no se pudo guardar el pedido. $err"
                            }
                        )
                    },
                    onError = { err -> checkoutError = err }
                )
            },
            onPayCard = { number, holder, month, year, cvv, document, amount ->
                checkoutError = null
                val email = cliente.correo?.takeIf { it.isNotBlank() } ?: "test_payer@example.com"
                val shippingAddr = selectedAddress.ifBlank { addresses.firstOrNull() ?: "Dirección principal" }
                viewModel.payWithCard(
                    cardNumber = number,
                    cardholder = holder,
                    expirationMonth = month,
                    expirationYear = year,
                    securityCode = cvv,
                    documentNumber = document,
                    amount = amount,
                    email = email,
                    onSuccess = {
                        viewModel.confirmOrder(
                            cliente = cliente,
                            address = shippingAddr,
                            onSuccess = { order -> paidOrder = order },
                            onError = { err ->
                                checkoutError = "El pago se aprobó, pero no se pudo guardar el pedido. $err"
                            }
                        )
                    },
                    onError = { err -> checkoutError = err }
                )
            }
        )
    } else if (showCartScreen) {
        CartScreen(
            isLoggedIn = true,
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
                val clean = newAddr.trim()
                if (clean.isNotBlank()) {
                    if (!addresses.contains(clean)) {
                        addresses.add(clean)
                    }
                    selectedAddress = clean
                    scope.launch {
                        cliente.id?.let { cId ->
                            try {
                                apiService.agregarDireccion(
                                    cId,
                                    DireccionDto(direccion = clean, esPrincipal = addresses.size == 1)
                                )
                                loadDirecciones()
                            } catch (e: Exception) {
                                orderError = null
                            }
                        }
                    }
                }
            },
            onBackClick = {
                showCartScreen = false
            },
            onConfirmOrder = {
                checkoutError = null
                showCheckoutScreen = true
            },
            onGoToLogin = {}
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
                                if (screen is DeliveryRoute.MisPedidos) {
                                    cliente.id?.let { cId ->
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
                        isLoggedIn = true,
                        userAddress = if (selectedAddress.isNotBlank()) selectedAddress else (addresses.firstOrNull() ?: "Selecciona una dirección"),
                        products = products,
                        activeOrders = activeOrders,
                        onCustomizeProduct = { product ->
                            customizingProduct = product
                        },
                        onAddToCart = { product ->
                            cartItems.add(CartItem(product, product.price, "", 1))
                        },
                        onLoginRequired = {},
                        onGoToActiveOrders = {
                            currentRoute = DeliveryRoute.MisPedidos
                        }
                    )
                    is DeliveryRoute.MisPedidos -> MisPedidosScreen(
                        isLoggedIn = true,
                        activeOrders = activeOrders,
                        historyOrders = historyOrders,
                        onRefresh = {
                            cliente.id?.let { cId ->
                                viewModel.fetchOrdersForCliente(cId)
                            }
                        },
                        onGoToLogin = {}
                    )
                    is DeliveryRoute.Configuracion -> ConfiguracionScreen(
                        darkModeEnabled = darkModeEnabled,
                        onDarkModeChange = onDarkModeChange
                    )
                    is DeliveryRoute.Usuario -> UsuarioScreen(
                        isLoggedInExternal = true,
                        cliente = cliente,
                        direcciones = direccionesState,
                        onAddDireccion = { dir, ref, principal ->
                            scope.launch {
                                cliente.id?.let { cId ->
                                    try {
                                        apiService.agregarDireccion(cId, DireccionDto(direccion = dir, referencia = ref, esPrincipal = principal))
                                        loadDirecciones()
                                    } catch (e: Exception) {
                                        orderError = "Error al agregar dirección: ${e.message}"
                                        showOrderErrorDialog = true
                                    }
                                }
                            }
                        },
                        onUpdateDireccion = { id, dir, ref, principal ->
                            scope.launch {
                                cliente.id?.let { cId ->
                                    try {
                                        apiService.actualizarDireccion(cId, id, DireccionDto(id = id, direccion = dir, referencia = ref, esPrincipal = principal))
                                        loadDirecciones()
                                    } catch (e: Exception) {
                                        orderError = "Error al actualizar dirección: ${e.message}"
                                        showOrderErrorDialog = true
                                    }
                                }
                            }
                        },
                        onDeleteDireccion = { id ->
                            scope.launch {
                                cliente.id?.let { cId ->
                                    try {
                                        apiService.eliminarDireccion(cId, id)
                                        loadDirecciones()
                                    } catch (e: Exception) {
                                        orderError = "Error al eliminar dirección: ${e.message}"
                                        showOrderErrorDialog = true
                                    }
                                }
                            }
                        },
                        onLogout = {
                            viewModel.stopWatchingOrders()
                            onLogout()
                        }
                    )
                }
            }
        }
    }

    paidOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Pago realizado", fontWeight = FontWeight.Bold) },
            text = {
                Text(order.details.ifBlank { "Tu pago ya se realizó." })
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOrderNotification(
                            "Pago realizado",
                            order.details.ifBlank { "Tu pago ya se realizó." }
                        )
                        paidOrder = null
                        showCheckoutScreen = false
                        showCartScreen = false
                        currentRoute = DeliveryRoute.MisPedidos
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Diálogo de error al crear el pedido o gestionar direcciones
    if (showOrderErrorDialog) {
        AlertDialog(
            onDismissRequest = { showOrderErrorDialog = false },
            title = { Text("Aviso del Sistema", fontWeight = FontWeight.Bold) },
            text = { Text(orderError ?: "Ocurrió un error en la operación.") },
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
}

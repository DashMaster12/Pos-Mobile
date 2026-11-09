package pe.edu.upeu.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.model.ClienteLoginRequest
import pe.edu.upeu.data.model.ClienteRequest
import pe.edu.upeu.data.model.ClienteResponse
import pe.edu.upeu.data.model.CreateOrderRequest
import pe.edu.upeu.data.model.OrderDetailRequest
import pe.edu.upeu.data.model.OrderResponse
import pe.edu.upeu.data.model.ProductDto
import pe.edu.upeu.data.model.ProductRef
import pe.edu.upeu.data.network.ApiService
import pe.edu.upeu.presentation.CartItem
import pe.edu.upeu.presentation.screens.OrderModel

class AppViewModel : ViewModel() {
    private val apiService = ApiService()

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    private val _activeOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val activeOrders: StateFlow<List<OrderModel>> = _activeOrders.asStateFlow()

    private val _historyOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val historyOrders: StateFlow<List<OrderModel>> = _historyOrders.asStateFlow()

    val cartItems = mutableStateListOf<CartItem>()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val remoteProducts = apiService.getProducts()
                _products.value = remoteProducts
                if (remoteProducts.isEmpty()) {
                    _errorMessage.value = "No se encontraron productos en el servidor."
                }
            } catch (e: Exception) {
                _products.value = emptyList()
                _errorMessage.value = "Error al conectar con el servidor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchOrdersForCliente(clienteId: Long) {
        viewModelScope.launch {
            try {
                val remoteOrders = apiService.getOrdersByCliente(clienteId)
                val mapped = remoteOrders.map { dtoToModel(it) }

                // Segregar entre pedidos activos e historial
                _activeOrders.value = mapped.filter { 
                    it.rawStatus == "EN_PREPARACION" || it.rawStatus == "EN_CAMINO" || it.status.contains("preparación", ignoreCase = true) || it.status.contains("camino", ignoreCase = true)
                }

                _historyOrders.value = mapped.filter { 
                    it.rawStatus == "ENTREGADO" || it.rawStatus == "CANCELADO" || it.status.contains("entregado", ignoreCase = true) || it.status.contains("cancelado", ignoreCase = true)
                }
            } catch (e: Exception) {
                // Muestra lista local si falla
            }
        }
    }

    private fun dtoToModel(dto: OrderResponse): OrderModel {
        val code = dto.orderNumber?.let { "#$it" } ?: "#${dto.id ?: 0}"
        val rawSt = dto.status ?: "EN_PREPARACION"
        
        val displayStatus = when (rawSt) {
            "EN_PREPARACION" -> "En preparación 🛵"
            "EN_CAMINO" -> "En camino 🚚"
            "ENTREGADO" -> "Entregado ✅"
            "CANCELADO" -> "Cancelado ❌"
            else -> rawSt
        }

        val itemsSummary = dto.details?.map { detail ->
            val pName = detail.product?.name ?: "Producto"
            val qty = detail.quantity ?: 1
            val price = detail.unitPrice ?: 0.0
            "${qty}x $pName ($${price})"
        } ?: emptyList()

        val summaryText = itemsSummary.joinToString(", ")

        return OrderModel(
            id = code,
            title = "Pedido $code",
            status = displayStatus,
            rawStatus = rawSt,
            totalStr = "$${dto.totalAmount ?: 0.0}0",
            details = summaryText,
            date = dto.creationDate?.take(16)?.replace("T", " ") ?: "Hoy, Recién realizado",
            address = dto.customerAddress ?: "Dirección principal",
            deliveryFee = "$5.00",
            itemsSummary = itemsSummary
        )
    }

    fun registerCliente(
        nombreCompleto: String,
        tipoDocumento: String,
        numeroDocumento: String,
        direccion: String,
        correo: String,
        password: String,
        onSuccess: (ClienteResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ClienteRequest(
                    nombreCompleto = nombreCompleto,
                    tipoDocumento = tipoDocumento,
                    numeroDocumento = numeroDocumento,
                    direccion = direccion,
                    correo = correo,
                    password = password
                )
                val response = apiService.registerCliente(request)
                onSuccess(response)
            } catch (e: Exception) {
                onError("No pudimos crear la cuenta. Revisa los datos e inténtalo de nuevo.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginCliente(
        identifier: String,
        password: String,
        onSuccess: (ClienteResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ClienteLoginRequest(
                    identifier = identifier,
                    password = password
                )
                val cliente = apiService.loginClienteWithCredentials(request)
                if (cliente != null && (cliente.id != null || !cliente.numeroDocumento.isNullOrBlank())) {
                    onSuccess(cliente)
                    cliente.id?.let { fetchOrdersForCliente(it) }
                } else {
                    onError("Credenciales incorrectas o cliente no encontrado.")
                }
            } catch (e: Exception) {
                onError("Error al iniciar sesión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateClienteAddress(
        cliente: ClienteResponse,
        newAddress: String,
        onSuccess: (ClienteResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val clienteId = cliente.id ?: return@launch onError("ID de cliente inválido")
                val request = ClienteRequest(
                    nombreCompleto = cliente.nombreCompleto ?: "Cliente",
                    tipoDocumento = cliente.tipoDocumento ?: "DNI",
                    numeroDocumento = cliente.numeroDocumento ?: "00000000",
                    direccion = newAddress,
                    correo = cliente.correo ?: "cliente@aleburgers.com",
                    password = ""
                )
                val updated = apiService.updateCliente(clienteId, request)
                onSuccess(updated)
            } catch (e: Exception) {
                onError("Error al actualizar dirección: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmOrder(
        cliente: ClienteResponse?,
        address: String,
        onSuccess: (OrderModel) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val subtotal = cartItems.sumOf { it.product.price * it.quantity }
            val deliveryFee = 5.0
            val total = subtotal + deliveryFee

            val detailsReq = cartItems.map {
                OrderDetailRequest(
                    product = ProductRef(id = it.product.id),
                    quantity = it.quantity
                )
            }

            val cId = cliente?.id ?: 1L

            val request = CreateOrderRequest(
                clienteId = cId,
                customerName = cliente?.nombreCompleto ?: "Cliente Alé Burgers",
                customerDocument = cliente?.numeroDocumento ?: "00000000",
                customerAddress = address,
                details = detailsReq
            )

            try {
                val response = apiService.createOrder(request)
                val code = response.orderNumber?.let { "#$it" } ?: "#${response.id ?: 0}"
                
                val itemsSummaryList = cartItems.map { "${it.quantity}x ${it.product.name} ($${it.product.price})" }
                val detailsText = cartItems.joinToString(", ") { "${it.quantity}x ${it.product.name}" }

                val newOrder = OrderModel(
                    id = code,
                    title = "Pedido $code",
                    status = "En preparación 🛵",
                    rawStatus = "EN_PREPARACION",
                    totalStr = "$${response.totalAmount ?: total}0",
                    details = detailsText,
                    date = "Hoy, Recién realizado",
                    address = address,
                    deliveryFee = "$5.00",
                    itemsSummary = itemsSummaryList
                )

                _activeOrders.value = listOf(newOrder) + _activeOrders.value
                cartItems.clear()
                _isLoading.value = false
                
                // Recargar desde servidor si se puede
                if (cId > 0) {
                    fetchOrdersForCliente(cId)
                }

                onSuccess(newOrder)
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error al procesar el pedido: ${e.message}")
            }
        }
    }
}

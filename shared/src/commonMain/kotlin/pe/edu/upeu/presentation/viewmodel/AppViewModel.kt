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
import pe.edu.upeu.data.model.ProductDto
import pe.edu.upeu.data.model.ProductRef
import pe.edu.upeu.data.network.ApiService
import pe.edu.upeu.presentation.CartItem
import pe.edu.upeu.presentation.screens.OrderModel

class AppViewModel : ViewModel() {
    private val apiService = ApiService()

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    val cartItems = mutableStateListOf<CartItem>()
    val ordersList = mutableStateListOf<OrderModel>()

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
                onError("Error al registrar cliente: ${e.message}")
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
        clienteId: Long,
        address: String,
        onSuccess: (OrderModel) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val subtotal = cartItems.sumOf { it.product.price * it.quantity }
            val deliveryFee = 5.0
            val total = subtotal + deliveryFee
            val orderCode = "#${1100 + ordersList.size}"

            val detailsReq = cartItems.map {
                OrderDetailRequest(
                    product = ProductRef(id = it.product.id),
                    quantity = it.quantity
                )
            }

            val request = CreateOrderRequest(
                clienteId = clienteId,
                orderType = "DELIVERY",
                details = detailsReq
            )

            try {
                val response = apiService.createOrder(request)
                
                val itemsSummaryList = cartItems.map { "${it.quantity}x ${it.product.name} ($${it.product.price})" }
                val detailsText = cartItems.joinToString(", ") { "${it.quantity}x ${it.product.name}" }

                val actualOrderCode = response.orderNumber?.let { "#$it" } ?: orderCode
                val actualTotal = response.totalAmount ?: total

                val newOrder = OrderModel(
                    id = actualOrderCode,
                    title = "Pedido $actualOrderCode",
                    status = "En preparación 🛵",
                    totalStr = "$${actualTotal}0",
                    details = detailsText,
                    date = "Hoy, Recién realizado",
                    address = address,
                    deliveryFee = "$${deliveryFee}0",
                    itemsSummary = itemsSummaryList
                )

                ordersList.add(0, newOrder)
                cartItems.clear()
                _isLoading.value = false
                onSuccess(newOrder)
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error al procesar el pedido: ${e.message}. (Verifica que haya una caja abierta en el POS web).")
            }
        }
    }
}

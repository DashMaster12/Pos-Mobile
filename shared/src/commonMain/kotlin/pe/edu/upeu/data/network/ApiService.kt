package pe.edu.upeu.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import pe.edu.upeu.data.model.ClienteLoginRequest
import pe.edu.upeu.data.model.ClienteRequest
import pe.edu.upeu.data.model.ClienteResponse
import pe.edu.upeu.data.model.CreateOrderRequest
import pe.edu.upeu.data.model.OrderResponse
import pe.edu.upeu.data.model.ProductDto

class ApiService {
    // API Gateway base URL en Render
    private val baseUrl = "https://ale-burgers-system.onrender.com"

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }
    }

    suspend fun getProducts(): List<ProductDto> {
        val response = client.get("$baseUrl/pos-venta-service/product")
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            val errorText = try { response.bodyAsText() } catch (e: Exception) { "Error ${response.status.value}" }
            throw Exception("HTTP ${response.status.value}: $errorText")
        }
    }

    suspend fun registerCliente(request: ClienteRequest): ClienteResponse {
        val response = client.post("$baseUrl/clientes-service/api/clientes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            val errorText = try { response.bodyAsText() } catch (e: Exception) { "Error ${response.status.value}" }
            throw Exception("Servidor (${response.status.value}): $errorText")
        }
    }

    suspend fun updateCliente(id: Long, request: ClienteRequest): ClienteResponse {
        val response = client.put("$baseUrl/clientes-service/api/clientes/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            val errorText = try { response.bodyAsText() } catch (e: Exception) { "Error ${response.status.value}" }
            throw Exception("Servidor (${response.status.value}): $errorText")
        }
    }

    suspend fun loginClienteByDocumento(numero: String): ClienteResponse? {
        return try {
            val response = client.get("$baseUrl/clientes-service/api/clientes/documento/$numero")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginClienteWithCredentials(request: ClienteLoginRequest): ClienteResponse? {
        return try {
            val response = client.post("$baseUrl/clientes-service/api/clientes/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createOrder(request: CreateOrderRequest): OrderResponse {
        val response = client.post("$baseUrl/pos-venta-service/order") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            val errorText = try { response.bodyAsText() } catch (e: Exception) { "Error ${response.status.value}" }
            throw Exception("Servidor (${response.status.value}): $errorText")
        }
    }

    suspend fun getOrdersByCliente(clienteId: Long): List<OrderResponse> {
        return try {
            val response = client.get("$baseUrl/pos-venta-service/order/cliente/$clienteId")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

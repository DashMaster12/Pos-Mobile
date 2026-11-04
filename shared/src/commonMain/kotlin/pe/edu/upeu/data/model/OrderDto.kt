package pe.edu.upeu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductRef(
    val id: Long
)

@Serializable
data class OrderDetailRequest(
    val product: ProductRef,
    val quantity: Int
)

@Serializable
data class CreateOrderRequest(
    val clienteId: Long,
    val orderType: String = "DELIVERY",
    val details: List<OrderDetailRequest>
)

@Serializable
data class OrderResponse(
    val id: Long,
    val orderNumber: Int? = null,
    val totalAmount: Double? = null,
    val customerName: String? = null
)

package pe.edu.upeu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductRef(
    val id: Long
)

@Serializable
data class OrderDetailRequest(
    val product: ProductRef,
    val quantity: Int,
    val unitPrice: Double? = null,
    val kitchenNotes: String? = null
)

@Serializable
data class CreateOrderRequest(
    val clienteId: Long,
    val customerName: String? = null,
    val customerDocument: String? = null,
    val customerAddress: String? = null,
    val details: List<OrderDetailRequest>
)

@Serializable
data class OrderDetailResponse(
    val id: Long? = null,
    val product: ProductDto? = null,
    val quantity: Int? = null,
    val unitPrice: Double? = null,
    val subtotal: Double? = null,
    val kitchenNotes: String? = null
)

@Serializable
data class OrderResponse(
    val id: Long? = null,
    val orderNumber: Int? = null,
    val status: String? = null,
    val totalAmount: Double? = null,
    val customerName: String? = null,
    val customerDocument: String? = null,
    val customerAddress: String? = null,
    val creationDate: String? = null,
    val details: List<OrderDetailResponse>? = null
)

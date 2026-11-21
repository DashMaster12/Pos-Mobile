package pe.edu.upeu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class YapePayRequest(
    val phoneNumber: String,
    val otp: String,
    val amount: Double,
    val email: String,
    val description: String
)

@Serializable
data class CardPayRequest(
    val cardNumber: String,
    val cardholder: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val securityCode: String,
    val documentNumber: String,
    val amount: Double,
    val email: String,
    val description: String
)

@Serializable
data class YapePayResponse(
    val approved: Boolean = false,
    val status: String? = null,
    val message: String? = null,
    val paymentId: Long? = null
)

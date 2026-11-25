package pe.edu.upeu.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.presentation.CartItem
import pe.edu.upeu.presentation.components.DeliveryButton
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    addresses: List<String>,
    selectedAddress: String,
    onSelectAddress: (String) -> Unit,
    onAddAddress: (direccion: String, referencia: String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onPayYape: (phone: String, otp: String, amount: Double) -> Unit,
    onPayCard: (number: String, holder: String, month: Int, year: Int, cvv: String, document: String, amount: Double) -> Unit
) {
    var payWithCard by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardholder by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }
    var securityCode by remember { mutableStateOf("") }
    var documentNumber by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    val deliveryFee = 5.0
    val total = subtotal + deliveryFee
    val shownError = localError ?: errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    SummaryRow("Productos", money(subtotal))
                    SummaryRow("Envío", money(deliveryFee))
                    Spacer(Modifier.height(6.dp))
                    SummaryRow("Total", money(total), emphasize = true)
                    if (!shownError.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            shownError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    DeliveryButton(
                        text = "Pagar ${money(total)}",
                        isLoading = isLoading,
                        onClick = {
                            val address = selectedAddress.ifBlank {
                                addresses.firstOrNull().orEmpty()
                            }
                            localError = when {
                                address.isBlank() -> "Falta la dirección. Vuelve al carrito y agrégala."
                                !payWithCard && phone.filter { it.isDigit() }.length != 9 ->
                                    "Ingresa el celular de Yape, de 9 dígitos."
                                !payWithCard && otp.filter { it.isDigit() }.length != 6 ->
                                    "Ingresa el código de aprobación de 6 dígitos."
                                payWithCard && cardNumber.filter { it.isDigit() }.length !in 15..16 ->
                                    "Elige una tarjeta de prueba o escribe un número válido."
                                payWithCard && cardholder.isBlank() -> "Falta el nombre del titular."
                                payWithCard && expirationMonth.toIntOrNull() !in 1..12 -> "El mes de vencimiento no es válido."
                                payWithCard && (expirationYear.toIntOrNull() ?: 0) < 24 -> "El año de vencimiento no es válido."
                                payWithCard && securityCode.filter { it.isDigit() }.length !in 3..4 -> "El CVV debe tener 3 o 4 dígitos."
                                payWithCard && documentNumber.filter { it.isDigit() }.length < 8 -> "El DNI debe tener al menos 8 dígitos."
                                else -> null
                            }
                            if (localError != null) return@DeliveryButton
                            if (selectedAddress.isBlank()) {
                                onSelectAddress(address)
                            }
                            if (addresses.none { it == address }) {
                                onAddAddress(address, "")
                            }
                            if (payWithCard) {
                                val year = expirationYear.toInt()
                                onPayCard(
                                    cardNumber.filter { it.isDigit() },
                                    cardholder.trim(),
                                    expirationMonth.toInt(),
                                    if (year < 100) 2000 + year else year,
                                    securityCode.filter { it.isDigit() },
                                    documentNumber.filter { it.isDigit() },
                                    total
                                )
                            } else {
                                onPayYape(phone.filter { it.isDigit() }, otp.filter { it.isDigit() }, total)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            item {
                SectionCard(title = "Entrega") {
                    val chosen = selectedAddress.ifBlank { addresses.firstOrNull().orEmpty() }
                    if (chosen.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                chosen,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        TextButton(onClick = onBack) {
                            Text("Cambiar dirección")
                        }
                    } else {
                        Text(
                            "Vuelve al carrito y agrega la dirección de entrega.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onBack) {
                            Text("Volver al carrito")
                        }
                    }
                }
            }
            item {
                SectionCard(title = "Tu pedido") {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}  ${item.product.name}", modifier = Modifier.weight(1f))
                            Text(money(item.unitPrice * item.quantity), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            item {
                SectionCard(title = "Pago") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !payWithCard, onClick = { payWithCard = false })
                        Text("Yape", modifier = Modifier.padding(end = 16.dp))
                        RadioButton(selected = payWithCard, onClick = { payWithCard = true })
                        Text("Tarjeta")
                    }
                    Spacer(Modifier.height(8.dp))
                    if (!payWithCard) {
                        Text(
                            "Prueba: celular 111111111 y código 123456.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it.filter { char -> char.isDigit() }.take(6) },
                            label = { Text("Código de aprobación") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it.filter { char -> char.isDigit() }.take(9) },
                            label = { Text("Celular Yape") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    } else {
                        Text(
                            "Elige una tarjeta de prueba. No se cobra dinero real.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        testCards.forEach { card ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cardNumber = card.number
                                        cardholder = card.holder
                                        expirationMonth = card.month
                                        expirationYear = card.year
                                        securityCode = card.cvv
                                        documentNumber = card.document
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = cardNumber == card.number,
                                    onClick = {
                                        cardNumber = card.number
                                        cardholder = card.holder
                                        expirationMonth = card.month
                                        expirationYear = card.year
                                        securityCode = card.cvv
                                        documentNumber = card.document
                                    }
                                )
                                Column {
                                    Text(card.label, fontWeight = FontWeight.Medium)
                                    Text(
                                        card.result,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it.filter { char -> char.isDigit() }.take(16) },
                            label = { Text("Número") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cardholder,
                            onValueChange = { cardholder = it },
                            label = { Text("Titular") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = expirationMonth,
                                onValueChange = { expirationMonth = it.filter { char -> char.isDigit() }.take(2) },
                                label = { Text("Mes") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = expirationYear,
                                onValueChange = { expirationYear = it.filter { char -> char.isDigit() }.take(4) },
                                label = { Text("Año") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = securityCode,
                                onValueChange = { securityCode = it.filter { char -> char.isDigit() }.take(4) },
                                label = { Text("CVV") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = documentNumber,
                            onValueChange = { documentNumber = it.filter { char -> char.isDigit() }.take(8) },
                            label = { Text("DNI") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    if (!shownError.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(shownError, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphasize: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (emphasize) 18.sp else 15.sp
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = if (emphasize) 18.sp else 15.sp,
            color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun money(value: Double): String {
    val cents = (value * 100).roundToInt()
    val soles = cents / 100
    val decimals = (cents % 100).toString().padStart(2, '0')
    return "S/ $soles.$decimals"
}

private data class TestCard(
    val label: String,
    val number: String,
    val holder: String,
    val month: String,
    val year: String,
    val cvv: String,
    val document: String,
    val result: String
)

private val testCards = listOf(
    TestCard("Mastercard", "5031755734530604", "APRO", "11", "2030", "123", "12345678", "Aprueba el pago"),
    TestCard("Visa", "4009175332806176", "OTHE", "11", "2030", "123", "12345678", "Rechaza el pago"),
    TestCard("Amex", "371180303257522", "FUND", "11", "2030", "1234", "12345678", "Rechaza por fondos")
)

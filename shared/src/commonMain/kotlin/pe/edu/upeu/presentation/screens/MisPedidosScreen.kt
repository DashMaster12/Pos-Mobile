package pe.edu.upeu.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.presentation.components.DeliveryButton

data class OrderModel(
    val id: String,
    val title: String,
    val status: String,
    val rawStatus: String = "EN_PREPARACION",
    val totalStr: String,
    val details: String,
    val date: String = "Hoy, 10:30 AM",
    val address: String = "Av. Principal 123, Lima",
    val deliveryFee: String = "$5.00",
    val itemsSummary: List<String> = listOf("1x Hamburguesa Deluxe ($25.00)")
)

@Composable
fun MisPedidosScreen(
    isLoggedIn: Boolean = true,
    activeOrders: List<OrderModel> = emptyList(),
    historyOrders: List<OrderModel> = emptyList(),
    onRefresh: () -> Unit = {},
    onGoToLogin: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mis Pedidos",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Sigue el estado en vivo de tus compras",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isLoggedIn) {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refrescar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoggedIn) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Inicia sesión para ver tus pedidos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Debes iniciar sesión para consultar el estado y detalle de tus órdenes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    DeliveryButton(
                        text = "Iniciar Sesión",
                        onClick = onGoToLogin,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Pestañas de selección: "En Curso" e "Historial"
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "En Curso (${activeOrders.size})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Historial (${historyOrders.size})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Pestaña En Curso: Mostrar unicamente pedidos activos (EN_PREPARACION, EN_CAMINO)
                    if (activeOrders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tienes pedidos en curso actualmente",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(activeOrders) { order ->
                                ExpandableOrderCard(order = order, isActive = true)
                            }
                        }
                    }
                }
                1 -> {
                    // Pestaña Historial: Mostrar pedidos finalizados (ENTREGADO, CANCELADO)
                    if (historyOrders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tienes historial de pedidos anteriores",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(historyOrders) { order ->
                                ExpandableOrderCard(order = order, isActive = false)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableOrderCard(
    order: OrderModel,
    isActive: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    val rawStatus = order.rawStatus.uppercase()
    val isPreparing = rawStatus == "EN_PREPARACION" || order.status.contains("preparación", ignoreCase = true)
    val isShipping = rawStatus == "EN_CAMINO" || order.status.contains("camino", ignoreCase = true)
    val isCanceled = rawStatus == "CANCELADO" || order.status.contains("cancelado", ignoreCase = true)

    // Color distintivo para el badge de estado
    val badgeContainerColor = when {
        isPreparing -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        isShipping -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        isCanceled -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        else -> Color(0xFFDCFCE7) // Verde claro para entregado
    }

    val badgeTextColor = when {
        isPreparing -> Color(0xFFB45309) // Ámbar oscuro
        isShipping -> MaterialTheme.colorScheme.primary
        isCanceled -> MaterialTheme.colorScheme.error
        else -> Color(0xFF166534) // Verde oscuro
    }

    val statusSubtitle = when {
        isPreparing -> "🛵 Tu pedido se está preparando en cocina..."
        isShipping -> "🚚 ¡El repartidor va en camino a tu dirección!"
        isCanceled -> "❌ Pedido cancelado por el restaurante"
        else -> "✅ Pedido completado y entregado"
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cabecera compacta de la tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = order.totalStr,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Badge de Estado
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeContainerColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo con mensaje animado según estado activo
            Text(
                text = statusSubtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = badgeTextColor
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Fila inferior de resumen con botón de expansión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Ocultar detalles" else "Ver detalles del pedido",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Desplegar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Contenido desplegable con animación suave
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    
                    // Sección: Productos
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Productos del Pedido", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (order.itemsSummary.isNotEmpty()) {
                        order.itemsSummary.forEach { item ->
                            Text(
                                text = "• $item",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                    } else if (order.details.isNotBlank()) {
                        Text(
                            text = "• ${order.details}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sección: Dirección de Envío
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Dirección de Envío", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(
                        text = order.address,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sección: Resumen de Pago
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Costo de Envío:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = order.deliveryFee, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total Cancelado:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = order.totalStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

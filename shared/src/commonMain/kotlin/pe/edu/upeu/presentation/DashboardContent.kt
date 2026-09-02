package pe.edu.upeu.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Resumen del día", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjetas apiladas verticalmente
        DashboardCard(
            title = "Estado de caja",
            content = "Caja abierta — Saldo inicial: S/ 100.00",
            actionText = "Gestionar caja",
            isPrimaryAction = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        DashboardCard(
            title = "Ventas de hoy",
            content = "S/ 0.00",
            contentStyle = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            actionText = "Ver reportes",
            isPrimaryAction = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        DashboardCard(
            title = "Acceso rápido",
            content = "Inicia una venta desde el punto de venta",
            actionText = "Ir al TPV",
            isPrimaryAction = true
        )
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    contentStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    actionText: String,
    isPrimaryAction: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(content, style = contentStyle)
            Spacer(modifier = Modifier.height(16.dp))

            if (isPrimaryAction) {
                Button(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                    Text(actionText)
                }
            } else {
                TextButton(onClick = { }) {
                    Text(actionText, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
package pe.edu.upeu.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OrdenesContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Órdenes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Historial de ventas", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(2) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Orden #${index + 1}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("S/ 37.50", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cliente: ${if (index == 0) "Mostrador" else "Varios"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Fecha: 7/3/26, 5:02 AM", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = { }) { Icon(Icons.Default.Info, contentDescription = "Detalles") }
                            IconButton(onClick = { }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}
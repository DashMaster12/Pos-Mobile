package pe.edu.upeu.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CajaContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Caja", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Apertura y cierre de caja", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Caja Actual ocupa todo el ancho arriba
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Caja actual", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Estado: Abierta", fontWeight = FontWeight.Bold)
                Text("Saldo inicial: S/ 100.00")
                Text("Apertura: 7/3/26, 5:02 AM")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                    Text("Cerrar caja")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Historial", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Lista de Historial debajo
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(1) {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Apertura #1", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Abierta", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Usuario: Administrador", style = MaterialTheme.typography.bodyMedium)
                        Text("Monto Inicial: S/ 100.00", style = MaterialTheme.typography.bodyMedium)
                        Text("Fecha: 7/3/26, 5:02 AM", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
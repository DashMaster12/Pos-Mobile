package pe.edu.upeu.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.presentation.components.DeliveryButton

@Composable
fun UsuarioScreen(onLogout: () -> Unit = {}) {
    // Estado de sesión simulado (por defecto true)
    var isLoggedIn by remember { mutableStateOf(true) }

    // Estados locales para direcciones y métodos de pago
    val addresses = remember { mutableStateListOf("Av. Principal 123, Lima", "Calle Los Pinos 456, Miraflores") }
    var newAddressInput by remember { mutableStateOf("") }

    val paymentMethods = remember { mutableStateListOf("Visa •••• 4242", "Yape / Plin •••• 9876") }
    var newPaymentInput by remember { mutableStateOf("") }

    // Diálogo de prueba para edición
    var showEditDialog by remember { mutableStateOf(false) }
    var editDialogTitle by remember { mutableStateOf("") }

    if (!isLoggedIn) {
        // Vista de usuario no logueado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No has iniciado sesión",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inicia sesión para gestionar tus pedidos, direcciones y métodos de pago.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                DeliveryButton(
                    text = "Iniciar Sesión",
                    onClick = { isLoggedIn = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        // Vista de usuario logueado con secciones interactivas
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera de Perfil
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Carlos Mendoza",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "carlos.mendoza@gmail.com",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "+51 987 654 321",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            editDialogTitle = "Editar Datos Personales"
                            showEditDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Sección: Gestionar Direcciones
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Gestionar Direcciones", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        addresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "• $addr", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                TextButton(onClick = {
                                    editDialogTitle = "Editar Dirección: $addr"
                                    showEditDialog = true
                                }) {
                                    Text("Editar", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newAddressInput,
                                onValueChange = { newAddressInput = it },
                                placeholder = { Text("Nueva dirección...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newAddressInput.isNotBlank()) {
                                        addresses.add(newAddressInput)
                                        newAddressInput = ""
                                    }
                                },
                                modifier = Modifier.height(52.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Agregar")
                            }
                        }
                    }
                }
            }

            // Sección: Métodos de Pago
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Métodos de Pago", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        paymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "• $method", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                TextButton(onClick = {
                                    editDialogTitle = "Gestionar Método: $method"
                                    showEditDialog = true
                                }) {
                                    Text("Gestionar", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newPaymentInput,
                                onValueChange = { newPaymentInput = it },
                                placeholder = { Text("Nuevo método (ej. Tarjeta/Yape)...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newPaymentInput.isNotBlank()) {
                                        paymentMethods.add(newPaymentInput)
                                        newPaymentInput = ""
                                    }
                                },
                                modifier = Modifier.height(52.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Agregar")
                            }
                        }
                    }
                }
            }

            // Botón de Cerrar Sesión
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DeliveryButton(
                    text = "Cerrar Sesión",
                    onClick = {
                        isLoggedIn = false
                        onLogout()
                    },
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Diálogo de alerta para funciones de prueba de edición
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(editDialogTitle) },
            text = { Text("Esta es una función de prueba interactiva para simular la edición de datos en la app de delivery.") },
            confirmButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

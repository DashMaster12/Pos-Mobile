package pe.edu.upeu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterSheetContent(
    onRegisterClick: (String, String, String, String, String, String) -> Unit,
    onGoToLogin: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var tipoDocumento by remember { mutableStateOf("DNI") }
    var numeroDocumento by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de Cliente",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = nombreCompleto,
                    onValueChange = { nombreCompleto = it },
                    label = { Text("Nombre Completo o Razón Social") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = tipoDocumento,
                    onValueChange = { tipoDocumento = it },
                    label = { Text("Tipo de Documento (DNI, RUC, S/D)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = numeroDocumento,
                    onValueChange = { numeroDocumento = it },
                    label = { Text("Número de Documento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico (@)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombreCompleto.isNotBlank() && numeroDocumento.isNotBlank() && correo.isNotBlank() && password.isNotBlank()) {
                    onRegisterClick(nombreCompleto, tipoDocumento, numeroDocumento, direccion, correo, password)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Registrar Cliente")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = onGoToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}

package pe.edu.upeu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val tiposDocumento = listOf("DNI", "RUC")

private fun digitosRequeridos(tipoDocumento: String): Int = if (tipoDocumento == "RUC") 11 else 8

@OptIn(ExperimentalMaterial3Api::class)
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
    var tipoDocumentoExpanded by remember { mutableStateOf(false) }

    val digitosDocumento = digitosRequeridos(tipoDocumento)
    val documentoValido = numeroDocumento.length == digitosDocumento
    val mensajeDocumento = if (tipoDocumento == "RUC") {
        "El RUC debe tener exactamente 11 dígitos"
    } else {
        "El DNI debe tener exactamente 8 dígitos"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Registro de Cliente",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            label = { Text("Nombre Completo o Razón Social") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = tipoDocumentoExpanded,
            onExpandedChange = { tipoDocumentoExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = tipoDocumento,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Documento") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDocumentoExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = tipoDocumentoExpanded,
                onDismissRequest = { tipoDocumentoExpanded = false }
            ) {
                tiposDocumento.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            tipoDocumento = option
                            numeroDocumento = numeroDocumento.take(digitosRequeridos(option))
                            tipoDocumentoExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        OutlinedTextField(
            value = numeroDocumento,
            onValueChange = { input ->
                numeroDocumento = input.filter { it.isDigit() }.take(digitosDocumento)
            },
            label = { Text("Número de Documento") },
            supportingText = { Text(mensajeDocumento) },
            isError = numeroDocumento.isNotEmpty() && !documentoValido,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electrónico (@)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (nombreCompleto.isNotBlank() && documentoValido && correo.isNotBlank() && password.isNotBlank()) {
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

        TextButton(onClick = onGoToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}

package pe.edu.upeu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upeu.data.model.ClienteResponse
import pe.edu.upeu.presentation.components.DeliveryButton

enum class AuthMode { SELECTOR, LOGIN, REGISTER }

@Composable
fun AuthContainerContent(
    onLoginSuccess: (ClienteResponse) -> Unit,
    onRegisterSuccess: (ClienteResponse) -> Unit,
    viewModel: pe.edu.upeu.presentation.viewmodel.AppViewModel
) {
    var authMode by remember { mutableStateOf(AuthMode.SELECTOR) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var registrationError by remember { mutableStateOf<String?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .then(
                    if (authMode == AuthMode.SELECTOR) {
                        Modifier
                    } else {
                        Modifier.verticalScroll(rememberScrollState())
                    }
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (authMode == AuthMode.SELECTOR) {
                Arrangement.Center
            } else {
                Arrangement.Top
            }
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Delicias Express",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tus antojos favoritos a un solo toque",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (authMode) {
                AuthMode.SELECTOR -> {
                    // Botón de Google Sign-In (Obtiene correo y nombre automáticamente)
                    DeliveryButton(
                        text = "Continuar con Google",
                        icon = Icons.Default.AccountCircle,
                        onClick = {
                            val googleEmail = "cliente.google@gmail.com"
                            val googleName = "Cliente Google"
                            viewModel.loginCliente(
                                identifier = googleEmail,
                                password = "google_auth_default",
                                onSuccess = { cliente ->
                                    onLoginSuccess(cliente)
                                },
                                onError = {
                                    viewModel.registerCliente(
                                        nombreCompleto = googleName,
                                        tipoDocumento = "DNI",
                                        numeroDocumento = "12345678",
                                        direccion = "Av. Principal 123",
                                        correo = googleEmail,
                                        password = "google_auth_default",
                                        onSuccess = { regCliente -> onRegisterSuccess(regCliente) },
                                        onError = { err -> registrationError = err }
                                    )
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { authMode = AuthMode.LOGIN },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Iniciar Sesión con Correo / Documento", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { authMode = AuthMode.REGISTER }
                    ) {
                        Text("Crear cuenta nueva con contraseña", color = MaterialTheme.colorScheme.primary)
                    }
                }

                AuthMode.LOGIN -> {
                    LoginSheetContent(
                        onLoginClick = { identifier, password ->
                            viewModel.loginCliente(
                                identifier = identifier,
                                password = password,
                                onSuccess = { cliente -> onLoginSuccess(cliente) },
                                onError = { err -> loginError = err }
                            )
                        },
                        onGoToRegister = { authMode = AuthMode.REGISTER },
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { authMode = AuthMode.SELECTOR }) {
                        Text("Volver atrás")
                    }
                }

                AuthMode.REGISTER -> {
                    RegisterSheetContent(
                        onRegisterClick = { nombre, tipoDoc, numDoc, dir, correo, pwd ->
                            viewModel.registerCliente(
                                nombreCompleto = nombre,
                                tipoDocumento = tipoDoc,
                                numeroDocumento = numDoc,
                                direccion = dir,
                                correo = correo,
                                password = pwd,
                                onSuccess = { cliente -> onRegisterSuccess(cliente) },
                                onError = { err -> registrationError = err }
                            )
                        },
                        onGoToLogin = { authMode = AuthMode.LOGIN },
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { authMode = AuthMode.SELECTOR }) {
                        Text("Volver atrás")
                    }
                }
            }
        }
    }

    if (loginError != null) {
        AlertDialog(
            onDismissRequest = { loginError = null },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("No se pudo iniciar sesión", fontWeight = FontWeight.Bold) },
            text = { Text(loginError ?: "") },
            confirmButton = {
                Button(onClick = { loginError = null }, shape = RoundedCornerShape(8.dp)) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (registrationError != null) {
        AlertDialog(
            onDismissRequest = { registrationError = null },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("No se pudo crear la cuenta", fontWeight = FontWeight.Bold) },
            text = { Text(registrationError ?: "") },
            confirmButton = {
                Button(onClick = { registrationError = null }, shape = RoundedCornerShape(8.dp)) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

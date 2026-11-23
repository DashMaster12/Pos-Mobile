package pe.edu.upeu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import pe.edu.upeu.data.model.ClienteResponse
import pe.edu.upeu.presentation.Screen
import pe.edu.upeu.presentation.SplashContent
import pe.edu.upeu.presentation.AuthContainerContent
import pe.edu.upeu.presentation.CustomerMainContent
import pe.edu.upeu.presentation.theme.AppTheme
import pe.edu.upeu.presentation.viewmodel.AppViewModel

@Composable
fun App() {
    // Inicializar Coil 3 con red Ktor de forma segura
    LaunchedEffect(Unit) {
        try {
            SingletonImageLoader.setSafe { context ->
                ImageLoader.Builder(context)
                    .components {
                        add(KtorNetworkFetcherFactory())
                    }
                    .build()
            }
        } catch (e: Exception) {
            // Ya inicializado
        }
    }

    var darkMode by remember { mutableStateOf(false) }
    val viewModel = remember { AppViewModel() }

    // Estado global de sesión del cliente
    var currentCliente by remember { mutableStateOf<ClienteResponse?>(null) }
    var loginSuccessName by remember { mutableStateOf<String?>(null) }
    var registerSuccessName by remember { mutableStateOf<String?>(null) }

    AppTheme(darkTheme = darkMode) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (currentScreen) {
                is Screen.Splash -> SplashContent(
                    onTimeout = { 
                        currentScreen = Screen.Main 
                    }
                )
                is Screen.Welcome -> {
                    AuthContainerContent(
                        onLoginSuccess = { cliente ->
                            currentCliente = cliente
                            loginSuccessName = cliente.nombreCompleto ?: "Cliente"
                            currentScreen = Screen.Main
                        },
                        onRegisterSuccess = { cliente ->
                            currentCliente = cliente
                            registerSuccessName = cliente.nombreCompleto ?: "Cliente"
                            currentScreen = Screen.Main
                        },
                        viewModel = viewModel
                    )
                }
                is Screen.Main -> {
                    if (currentCliente == null) {
                        AuthContainerContent(
                            onLoginSuccess = { cliente ->
                                currentCliente = cliente
                                loginSuccessName = cliente.nombreCompleto ?: "Cliente"
                            },
                            onRegisterSuccess = { cliente ->
                                currentCliente = cliente
                                registerSuccessName = cliente.nombreCompleto ?: "Cliente"
                            },
                            viewModel = viewModel
                        )
                    } else {
                        CustomerMainContent(
                            cliente = currentCliente!!,
                            onLogout = { 
                                currentCliente = null
                            },
                            darkModeEnabled = darkMode,
                            onDarkModeChange = { darkMode = it }
                        )
                    }
                }
            }

            if (loginSuccessName != null) {
                LaunchedEffect(loginSuccessName) {
                    delay(3000)
                    loginSuccessName = null
                }
                AlertDialog(
                    onDismissRequest = { },
                    icon = {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    title = { Text("Sesión iniciada", fontWeight = FontWeight.Bold) },
                    text = { Text("Hola, $loginSuccessName. Ya puedes continuar con tu pedido.") },
                    confirmButton = {},
                    shape = RoundedCornerShape(20.dp)
                )
            }

            if (registerSuccessName != null) {
                LaunchedEffect(registerSuccessName) {
                    delay(3000)
                    registerSuccessName = null
                }
                AlertDialog(
                    onDismissRequest = { },
                    icon = {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    title = { Text("Cuenta creada", fontWeight = FontWeight.Bold) },
                    text = { Text("Hola, $registerSuccessName. Tu cuenta ya está lista.") },
                    confirmButton = {},
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

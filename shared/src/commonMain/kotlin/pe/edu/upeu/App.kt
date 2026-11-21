package pe.edu.upeu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                            currentScreen = Screen.Main
                        },
                        onRegisterSuccess = { cliente ->
                            currentCliente = cliente
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
                            },
                            onRegisterSuccess = { cliente ->
                                currentCliente = cliente
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
        }
    }
}

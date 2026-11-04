package pe.edu.upeu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import pe.edu.upeu.presentation.Screen
import pe.edu.upeu.presentation.SplashContent
import pe.edu.upeu.presentation.WelcomeContent
import pe.edu.upeu.presentation.CustomerMainContent
import pe.edu.upeu.presentation.theme.AppTheme

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
    var darkModeEnabled by remember { mutableStateOf(false) }

    AppTheme(darkTheme = darkModeEnabled) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (currentScreen) {
                is Screen.Splash -> SplashContent(
                    onTimeout = { currentScreen = Screen.Welcome }
                )
                is Screen.Welcome -> WelcomeContent(
                    onStartClick = { currentScreen = Screen.Main }
                )
                is Screen.Main -> CustomerMainContent(
                    onLogout = { currentScreen = Screen.Welcome },
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeChange = {darkModeEnabled = it}
                )
            }
        }
    }
}

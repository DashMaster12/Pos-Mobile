package pe.edu.upeu.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorSchema = lightColorScheme(
    primary = PrimaryCyan,
    onPrimary = TextLight,
    primaryContainer = CyanContainer,
    background = BackgroundLight,
    surface = BackgroundLight,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorSchema,
        // Usamos la tipografía nativa de Material 3 por defecto
        content = content
    )
}
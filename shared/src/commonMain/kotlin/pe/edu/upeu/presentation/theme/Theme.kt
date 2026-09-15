package pe.edu.upeu.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DeliveryLightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = OnPrimaryLight,
    primaryContainer = OrangeContainer,
    secondary = SecondaryAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextDark,
    onSurface = TextDark,
    outline = OutlineColor
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DeliveryLightColorScheme,
        typography = AppTypography,
        content = content
    )
}

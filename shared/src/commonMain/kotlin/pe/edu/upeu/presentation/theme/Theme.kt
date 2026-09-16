package pe.edu.upeu.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AleLightColorScheme = lightColorScheme(
    primary = AleBluePrimary,
    onPrimary = AleSurfaceLight,
    primaryContainer = AleBackgroundLight,
    onPrimaryContainer = AleBluePrimary,
    secondary = AleYellowSecondary,
    onSecondary = AleTextDark,
    tertiary = AleRedTertiary,
    onTertiary = AleSurfaceLight,
    background = AleBackgroundLight,
    surface = AleSurfaceLight,
    onBackground = AleTextDark,
    onSurface = AleTextDark,
    outline = AleOutline
)

private val AleDarkColorScheme = darkColorScheme(
    primary = AleBluePrimary,
    onPrimary = AleSurfaceLight,
    primaryContainer = AleSurfaceDark,
    onPrimaryContainer = AleYellowSecondary,
    secondary = AleYellowSecondary,
    onSecondary = AleTextDark,
    tertiary = AleRedTertiary,
    onTertiary = AleSurfaceLight,
    background = AleBackgroundDark,
    surface = AleSurfaceDark,
    onBackground = AleTextLight,
    onSurface = AleTextLight,
    outline = AleOutline
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AleDarkColorScheme else AleLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

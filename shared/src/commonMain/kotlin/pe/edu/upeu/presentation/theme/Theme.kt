package pe.edu.upeu.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = CyanPrimary,
    onPrimary = CyanOnPrimary,
    primaryContainer = CyanPrimaryContainer,
    onPrimaryContainer = CyanOnPrimaryContainer,
    secondary = CyanSecondary,
    onSecondary = CyanOnSecondary,
    secondaryContainer = CyanSecondaryContainer,
    onSecondaryContainer = CyanOnSecondaryContainer,
    tertiary = AzureTertiary,
    onTertiary = AzureOnTertiary,
    tertiaryContainer = AzureTertiaryContainer,
    onTertiaryContainer = AzureOnTertiaryContainer,
    background = CyanBackground,
    onBackground = CyanOnSurface,
    surface = CyanSurface,
    onSurface = CyanOnSurface,
    surfaceVariant = CyanSurfaceVariant,
    onSurfaceVariant = CyanOnSurfaceVariant,
    outline = CyanOutline,
    outlineVariant = CyanOutlineVariant,
    inverseSurface = CyanInverseSurface,
    inverseOnSurface = CyanInverseOnSurface,
    inversePrimary = CyanInversePrimary,
    error = CyanError,
    onError = CyanOnError,
    errorContainer = CyanErrorContainer,
    onErrorContainer = CyanOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimaryDark,
    onPrimary = CyanOnPrimaryDark,
    primaryContainer = CyanPrimaryContainerDark,
    onPrimaryContainer = CyanOnPrimaryContainerDark,
    secondary = CyanSecondaryDark,
    onSecondary = CyanOnSecondaryDark,
    secondaryContainer = CyanSecondaryContainerDark,
    onSecondaryContainer = CyanOnSecondaryContainerDark,
    tertiary = AzureTertiaryDark,
    onTertiary = AzureOnTertiaryDark,
    tertiaryContainer = AzureTertiaryContainerDark,
    onTertiaryContainer = AzureOnTertiaryContainerDark,
    background = CyanBackgroundDark,
    onBackground = CyanOnSurfaceDark,
    surface = CyanSurfaceDark,
    onSurface = CyanOnSurfaceDark,
    surfaceVariant = CyanSurfaceVariantDark,
    onSurfaceVariant = CyanOnSurfaceVariantDark,
    outline = CyanOutlineDark,
    outlineVariant = CyanOutlineVariantDark,
    inverseSurface = CyanInverseSurfaceDark,
    inverseOnSurface = CyanInverseOnSurfaceDark,
    inversePrimary = CyanInversePrimaryDark,
    error = CyanErrorDark,
    onError = CyanOnErrorDark,
    errorContainer = CyanErrorContainerDark,
    onErrorContainer = CyanOnErrorContainerDark
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

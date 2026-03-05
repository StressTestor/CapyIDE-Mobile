package dev.capyide.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CapyIndigo,
    onPrimary = CapyOnPrimary,
    secondary = CapyFur,
    onSecondary = CapyMidnight,
    tertiary = CapyFurDeep,
    background = CapyMidnight,
    onBackground = CapyCream,
    surface = CapyMidnight,
    onSurface = CapyCream,
    surfaceVariant = CapyIndigoDark,
    outline = CapyCream.copy(alpha = 0.4f)
)

private val LightColorScheme = lightColorScheme(
    primary = CapyPrimary,
    onPrimary = CapyOnPrimary,
    secondary = CapySecondary,
    onSecondary = CapyOnSecondary,
    tertiary = CapyTertiary,
    background = CapyBackground,
    onBackground = CapyOnBackground,
    surface = CapySurface,
    onSurface = CapyOnSurface,
    surfaceVariant = CapySurfaceVariant,
    outline = CapyOutline
)

@Composable
fun CapyIDEMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

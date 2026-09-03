package com.calculatorlife.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueContainerLight,
    onPrimaryContainer = BluePrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = BlueContainerDark,
    onPrimaryContainer = BluePrimaryDark,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = ErrorRed
)

/**
 * App-wide theme. [darkTheme] is driven by the Settings module (Phase 7);
 * until then it defaults to the system setting, with the spec's default
 * being light/white.
 */
@Composable
fun CalculatorLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = CalculatorTypography,
        content = content
    )
}

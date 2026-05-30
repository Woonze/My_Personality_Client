package com.example.mypersonality.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Navy,
    secondary = Blue,
    tertiary = Coral,
    background = LightSurface,
    surface = Sand
)

private val DarkColors = darkColorScheme(
    primary = Sand,
    secondary = Blue,
    tertiary = Coral,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun MyPersonalityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}

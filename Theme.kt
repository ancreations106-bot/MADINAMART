package com.madinamart.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = White,
    primaryContainer = Emerald50,
    onPrimaryContainer = Emerald900,
    secondary = Amber600,
    onSecondary = White,
    secondaryContainer = Amber50,
    onSecondaryContainer = Neutral900,
    background = Neutral50,
    onBackground = Neutral950,
    surface = White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral600,
    outline = Neutral300,
    error = Rose600,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald600,
    onPrimary = White,
    primaryContainer = Emerald900,
    onPrimaryContainer = Emerald100,
    secondary = Amber500,
    onSecondary = Neutral950,
    background = Neutral950,
    onBackground = Neutral100,
    surface = Neutral900,
    onSurface = Neutral100,
    surfaceVariant = Neutral800,
    onSurfaceVariant = Neutral400,
    outline = Neutral700,
    error = Rose500,
    onError = White
)

@Composable
fun MadinaMartTheme(
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

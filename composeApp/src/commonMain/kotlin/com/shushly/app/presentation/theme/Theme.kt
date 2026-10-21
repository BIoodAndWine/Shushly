package com.shushly.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ShushlySapphireLight,
    onPrimary = Color.White,
    primaryContainer = ShushlySapphireDark,
    onPrimaryContainer = Color.White,
    secondary = ShushlyEmerald,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = ShushlyCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ShushlySapphire,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = ShushlySapphireDark,
    secondary = ShushlyEmeraldDark,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = ShushlyCrimson,
    onError = Color.White
)

@Composable
fun ShushlyTheme(
    themeMode: String = "dark",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

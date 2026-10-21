package com.shushly.app.presentation.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    themeMode: String = "system",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val targetColors = if (isDark) DarkColorScheme else LightColorScheme
    val animationSpec = tween<Color>(durationMillis = 280, easing = FastOutSlowInEasing)

    val animatedBackground by animateColorAsState(targetColors.background, animationSpec, label = "ThemeBackground")
    val animatedSurface by animateColorAsState(targetColors.surface, animationSpec, label = "ThemeSurface")
    val animatedSurfaceVariant by animateColorAsState(targetColors.surfaceVariant, animationSpec, label = "ThemeSurfaceVariant")
    val animatedOnBackground by animateColorAsState(targetColors.onBackground, animationSpec, label = "ThemeOnBackground")
    val animatedOnSurface by animateColorAsState(targetColors.onSurface, animationSpec, label = "ThemeOnSurface")
    val animatedOnSurfaceVariant by animateColorAsState(targetColors.onSurfaceVariant, animationSpec, label = "ThemeOnSurfaceVariant")
    val animatedPrimary by animateColorAsState(targetColors.primary, animationSpec, label = "ThemePrimary")
    val animatedOutline by animateColorAsState(targetColors.outline, animationSpec, label = "ThemeOutline")

    val animatedColorScheme = targetColors.copy(
        background = animatedBackground,
        surface = animatedSurface,
        surfaceVariant = animatedSurfaceVariant,
        onBackground = animatedOnBackground,
        onSurface = animatedOnSurface,
        onSurfaceVariant = animatedOnSurfaceVariant,
        primary = animatedPrimary,
        outline = animatedOutline
    )

    MaterialTheme(
        colorScheme = animatedColorScheme,
        content = content
    )
}

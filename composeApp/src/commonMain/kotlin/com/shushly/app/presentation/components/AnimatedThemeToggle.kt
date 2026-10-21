package com.shushly.app.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Animated theme toggle button inspired by https://karlmolles.com/
 *
 * Provides a fluid transformation between Sun and Moon states with
 * coupled rotation, scale, and opacity driven by natural spring physics.
 *
 * In Dark Mode: Displays the Sun icon (inviting switch to Light).
 * In Light Mode: Displays the Moon icon (inviting switch to Dark).
 */
@Composable
fun AnimatedThemeToggle(
    isDark: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile press scale feedback
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ThemeTogglePressScale"
    )

    // Sun Icon Transitions (Visible in Dark Mode)
    val sunRotation by animateFloatAsState(
        targetValue = if (isDark) 0f else -90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "SunRotation"
    )

    val sunScale by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "SunScale"
    )

    val sunAlpha by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "SunAlpha"
    )

    // Moon Icon Transitions (Visible in Light Mode)
    val moonRotation by animateFloatAsState(
        targetValue = if (!isDark) 0f else 90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "MoonRotation"
    )

    val moonScale by animateFloatAsState(
        targetValue = if (!isDark) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "MoonScale"
    )

    val moonAlpha by animateFloatAsState(
        targetValue = if (!isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "MoonAlpha"
    )

    val accessibilityLabel = if (isDark) "Switch to light appearance" else "Switch to dark appearance"

    Box(
        modifier = modifier
            .size(42.dp)
            .scale(pressScale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .semantics {
                role = Role.Button
                contentDescription = accessibilityLabel
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            ),
        contentAlignment = Alignment.Center
    ) {
        // Sun Icon (Warm golden radiance when dark mode is on)
        if (sunAlpha > 0.01f) {
            Icon(
                imageVector = Icons.Default.LightMode,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier
                    .size(22.dp)
                    .rotate(sunRotation)
                    .scale(sunScale)
                    .alpha(sunAlpha)
            )
        }

        // Moon Icon (Subtle slate/sapphire lunar crescent when light mode is on)
        if (moonAlpha > 0.01f) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = Color(0xFF475569),
                modifier = Modifier
                    .size(22.dp)
                    .rotate(moonRotation)
                    .scale(moonScale)
                    .alpha(moonAlpha)
            )
        }
    }
}

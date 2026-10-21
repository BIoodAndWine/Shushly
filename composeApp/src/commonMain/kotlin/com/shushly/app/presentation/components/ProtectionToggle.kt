package com.shushly.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.StatusState
import com.shushly.app.presentation.theme.DarkBorder
import com.shushly.app.presentation.theme.ShushlyAmber
import com.shushly.app.presentation.theme.ShushlyCrimson
import com.shushly.app.presentation.theme.ShushlyEmerald
import com.shushly.app.presentation.theme.ShushlyEmeraldGlow
import com.shushly.app.presentation.theme.ShushlySapphire
import com.shushly.app.presentation.theme.ShushlySapphireLight

@Composable
fun ProtectionToggle(
    status: ProtectionStatus,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth press feedback with tactile spring physics
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    // Animated colors based on status
    val ringColor by animateColorAsState(
        targetValue = when (status.state) {
            StatusState.CONNECTED -> ShushlyEmerald
            StatusState.CONNECTING -> ShushlySapphireLight
            StatusState.RECONNECTING -> ShushlyAmber
            StatusState.ERROR -> ShushlyCrimson
            StatusState.DISABLED -> DarkBorder
        },
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
    )

    // Surface-adaptive background
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val innerBackgroundColor by animateColorAsState(
        targetValue = when (status.state) {
            StatusState.CONNECTED -> if (isDark) Color(0xFF0D251D) else Color(0xFFD1FAE5)
            StatusState.CONNECTING -> if (isDark) Color(0xFF132238) else Color(0xFFDBEAFE)
            StatusState.RECONNECTING -> if (isDark) Color(0xFF281F10) else Color(0xFFFEF3C7)
            StatusState.ERROR -> if (isDark) Color(0xFF281014) else Color(0xFFFEE2E2)
            StatusState.DISABLED -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 280)
    )

    val iconColor by animateColorAsState(
        targetValue = when (status.state) {
            StatusState.CONNECTED -> if (isDark) ShushlyEmerald else Color(0xFF047857)
            StatusState.CONNECTING -> if (isDark) ShushlySapphireLight else ShushlySapphire
            StatusState.RECONNECTING -> ShushlyAmber
            StatusState.ERROR -> ShushlyCrimson
            StatusState.DISABLED -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        animationSpec = tween(durationMillis = 280)
    )

    // Pulsing halo for connecting / reconnecting states
    val infiniteTransition = rememberInfiniteTransition()
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring during transition or active state
        if (status.isTransitioning) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(haloPulse)
                    .clip(CircleShape)
                    .background(
                        if (status.state == StatusState.CONNECTING) ShushlySapphire.copy(alpha = haloAlpha)
                        else ShushlyAmber.copy(alpha = haloAlpha)
                    )
            )
        } else if (status.isProtected) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(ShushlyEmeraldGlow)
            )
        }

        // Main Button Surface
        Box(
            modifier = Modifier
                .size(190.dp)
                .scale(pressScale)
                .clip(CircleShape)
                .background(innerBackgroundColor)
                .border(
                    width = if (status.isProtected || status.isTransitioning) 3.dp else 1.5.dp,
                    color = ringColor,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggle
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (status.isTransitioning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(44.dp),
                        color = ringColor,
                        strokeWidth = 3.dp
                    )
                } else if (status.state == StatusState.ERROR) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Connection Error",
                        tint = iconColor,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = if (status.isProtected) "Disable Protection" else "Enable Protection",
                        tint = iconColor,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when (status.state) {
                        StatusState.CONNECTED -> "ON"
                        StatusState.CONNECTING -> "CONNECTING"
                        StatusState.RECONNECTING -> "RESUMING"
                        StatusState.ERROR -> "RETRY"
                        StatusState.DISABLED -> "OFF"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = iconColor
                )
            }
        }
    }
}

// Luminance helper extension for Compose Color
private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

package com.shushly.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.StatusState
import com.shushly.app.presentation.theme.DarkBorder
import com.shushly.app.presentation.theme.ShushlyAmber
import com.shushly.app.presentation.theme.ShushlyCrimson
import com.shushly.app.presentation.theme.ShushlySapphire
import com.shushly.app.presentation.theme.ShushlySapphireDark
import com.shushly.app.presentation.theme.ShushlySapphireLight

/**
 * Clean, prominent horizontal pill toggle inspired by Cloudflare WARP,
 * styled specifically with Shushly's brand identity.
 *
 * Dimensions: 144dp width x 74dp height, with a 62dp circular thumb
 * that slides smoothly using natural spring physics.
 */
@Composable
fun ProtectionToggle(
    status: ProtectionStatus,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile press response with spring physics
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "PressScale"
    )

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Track background color based on connection state
    val trackColor by animateColorAsState(
        targetValue = when (status.state) {
            StatusState.CONNECTED -> ShushlySapphire
            StatusState.CONNECTING -> if (isDark) ShushlySapphireDark else ShushlySapphireLight
            StatusState.RECONNECTING -> ShushlyAmber
            StatusState.ERROR -> ShushlyCrimson
            StatusState.DISABLED -> if (isDark) Color(0xFF192231) else Color(0xFFE2E8F0)
        },
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "TrackColor"
    )

    // Track border for definition when disabled
    val trackBorderColor by animateColorAsState(
        targetValue = when (status.state) {
            StatusState.CONNECTED -> ShushlySapphireDark
            StatusState.CONNECTING -> ShushlySapphireLight
            StatusState.RECONNECTING -> ShushlyAmber
            StatusState.ERROR -> ShushlyCrimson
            StatusState.DISABLED -> if (isDark) DarkBorder else Color(0xFFCBD5E1)
        },
        animationSpec = tween(durationMillis = 280),
        label = "TrackBorder"
    )

    // Thumb position offset:
    // Track width: 144dp, Track padding: 6dp -> Inner width: 132dp.
    // Thumb size: 62dp.
    // Travel distance = 132dp - 62dp = 70dp.
    val isTargetOn = status.state == StatusState.CONNECTED || status.state == StatusState.CONNECTING
    val thumbOffsetTarget = if (isTargetOn) 70.dp else 0.dp

    val thumbOffset by animateDpAsState(
        targetValue = thumbOffsetTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ThumbOffset"
    )

    // Gentle halo transition for connecting state
    val infiniteTransition = rememberInfiniteTransition(label = "ConnectingTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val trackShape = RoundedCornerShape(37.dp)

    Box(
        modifier = modifier
            .scale(pressScale)
            .semantics { role = Role.Switch }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Outer glow when connecting or protected
        if (status.state == StatusState.CONNECTING) {
            Box(
                modifier = Modifier
                    .size(width = 152.dp, height = 82.dp)
                    .clip(RoundedCornerShape(41.dp))
                    .background(ShushlySapphireLight.copy(alpha = pulseAlpha))
            )
        }

        // Track surface
        Box(
            modifier = Modifier
                .size(width = 144.dp, height = 74.dp)
                .clip(trackShape)
                .background(trackColor)
                .border(
                    width = if (status.state == StatusState.DISABLED) 1.5.dp else 1.dp,
                    color = trackBorderColor,
                    shape = trackShape
                )
                .padding(6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Floating Thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(62.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                when (status.state) {
                    StatusState.CONNECTED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Connected",
                            tint = ShushlySapphire,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    StatusState.CONNECTING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ShushlySapphire,
                            strokeWidth = 2.5.dp
                        )
                    }
                    StatusState.RECONNECTING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ShushlyAmber,
                            strokeWidth = 2.5.dp
                        )
                    }
                    StatusState.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry Connection",
                            tint = ShushlyCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    StatusState.DISABLED -> {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Protection Off",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

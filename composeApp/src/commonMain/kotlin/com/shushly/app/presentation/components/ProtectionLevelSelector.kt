package com.shushly.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.presentation.theme.DarkBorder
import com.shushly.app.presentation.theme.ShushlySapphire
import com.shushly.app.presentation.theme.ShushlySapphireLight

@Composable
fun ProtectionLevelSelector(
    selectedLevel: ProtectionLevel,
    onLevelSelected: (ProtectionLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Protection Mode",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedContent(
                targetState = selectedLevel,
                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) }
            ) { level ->
                Text(
                    text = when (level) {
                        ProtectionLevel.LIGHT -> "Maximum Compatibility"
                        ProtectionLevel.BALANCED -> "Balanced Coverage"
                        ProtectionLevel.STRICT -> "Strict Privacy"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ShushlySapphire
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProtectionLevel.entries.forEach { level ->
                val isSelected = selectedLevel == level
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val pressScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )

                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) ShushlySapphireLight else DarkBorder,
                    animationSpec = tween(200)
                )
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                    animationSpec = tween(200)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(pressScale)
                        .clip(RoundedCornerShape(14.dp))
                        .background(backgroundColor)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onLevelSelected(level) }
                        )
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (level == ProtectionLevel.LIGHT) {
                            Text(
                                text = "DEFAULT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = ShushlySapphire
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        } else {
                            Spacer(modifier = Modifier.height(13.dp))
                        }

                        Text(
                            text = level.title,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Single calm explanatory sentence
        AnimatedContent(
            targetState = selectedLevel,
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) }
        ) { level ->
            Text(
                text = level.subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

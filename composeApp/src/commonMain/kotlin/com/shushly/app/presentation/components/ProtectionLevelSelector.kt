package com.shushly.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        Text(
            text = "Protection Level",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProtectionLevel.entries.forEach { level ->
                val isSelected = selectedLevel == level
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) ShushlySapphireLight else DarkBorder
                )
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(backgroundColor)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onLevelSelected(level) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (level == ProtectionLevel.BALANCED) {
                            Text(
                                text = "RECOMMENDED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = ShushlySapphire
                            )
                            Spacer(modifier = Modifier.height(2.dp))
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
    }
}

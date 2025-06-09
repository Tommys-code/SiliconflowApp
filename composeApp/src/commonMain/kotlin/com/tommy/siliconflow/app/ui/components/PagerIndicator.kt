package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.ui.theme.AppTheme

@Composable
fun PagerIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until count) {
            Indicator(selected = i == current)
        }
    }
}

@Composable
private fun Indicator(
    size: Dp = 6.dp,
    color: Color = AppTheme.colorScheme.iconContainer,
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (selected) color else color.copy(alpha = 0.5f))
    )
}
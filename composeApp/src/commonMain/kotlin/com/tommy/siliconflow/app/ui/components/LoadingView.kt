package com.tommy.siliconflow.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tommy.siliconflow.app.ui.theme.AppTheme

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.size(120.dp)
            .clip(
                RoundedCornerShape(
                    12.dp
                )
            )
            .background(AppTheme.colorScheme.inverseBackground.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
fun LoadingDialog(showDialog: MutableState<Boolean>) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = {},
        ) {
            LoadingView()
        }
    }
}

@Composable
fun ThreeDotLoading(
    dotCount: Int = 3,
    delayPerDot: Int = 200,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alphas = (0 until dotCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = dotCount * delayPerDot * 2
                    0.2f at 0
                    1f at delayPerDot with LinearEasing
                    0.2f at delayPerDot * 2
                },
                initialStartOffset = StartOffset(index * delayPerDot)
            )
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        alphas.forEach { alpha ->
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(8.dp)
                    .graphicsLayer(alpha = alpha.value)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
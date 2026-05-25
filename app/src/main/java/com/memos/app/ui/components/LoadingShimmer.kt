package com.memos.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
private fun shimmerBrush(): Brush {
    val colors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1200f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "x"
    )
    return Brush.linearGradient(colors, Offset(x - 600, 0f), Offset(x, 0f))
}

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier,
    brush: Brush
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}

@Composable
fun MemoCardShimmer() {
    val brush = shimmerBrush()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ShimmerBox(Modifier.width(100.dp).height(14.dp), brush)
            ShimmerBox(Modifier.width(60.dp).height(14.dp), brush)
        }
        Spacer(Modifier.height(14.dp))
        repeat(3) { i ->
            ShimmerBox(
                Modifier.fillMaxWidth(if (i == 2) 0.55f else 1f).height(13.dp),
                brush
            )
            if (i < 2) Spacer(Modifier.height(7.dp))
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                ShimmerBox(Modifier.width(64.dp).height(24.dp).clip(RoundedCornerShape(12.dp)), brush)
            }
        }
    }
}

@Composable
fun ShimmerList(count: Int = 6) {
    Column {
        repeat(count) { MemoCardShimmer() }
    }
}

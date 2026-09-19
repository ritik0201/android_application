package com.example.myapplication.ui.components.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated flickering flame illustration for streak counters using Compose Canvas.
 */
@Composable
fun AnimatedFlameAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FlameFlicker")

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameOuter"
    )

    val innerScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameInner"
    )

    val orangeColor = MaterialTheme.colorScheme.tertiary
    val yellowColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer Flame Path
        val outerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.05f * outerScale)
            cubicTo(
                w * 0.85f * outerScale, h * 0.35f,
                w * 0.95f, h * 0.75f,
                w * 0.5f, h * 0.98f
            )
            cubicTo(
                w * 0.05f, h * 0.75f,
                w * 0.15f * outerScale, h * 0.35f,
                w * 0.5f, h * 0.05f * outerScale
            )
            close()
        }
        drawPath(outerPath, color = orangeColor)

        // Inner Flame Path
        val innerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.35f * innerScale)
            cubicTo(
                w * 0.7f, h * 0.55f,
                w * 0.75f, h * 0.8f,
                w * 0.5f, h * 0.95f
            )
            cubicTo(
                w * 0.25f, h * 0.8f,
                w * 0.3f, h * 0.55f,
                w * 0.5f, h * 0.35f * innerScale
            )
            close()
        }
        drawPath(innerPath, color = yellowColor)
    }
}

package com.example.myapplication.ui.components.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * Animated water glass with a rising wave effect drawn with Compose Canvas.
 */
@Composable
fun WaterGlassWaveAnimation(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    waterColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "WaterLevelProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "WaveOscillation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val glassWidth = w * 0.7f
        val glassHeight = h * 0.85f
        val glassLeft = (w - glassWidth) / 2f
        val glassTop = (h - glassHeight) / 2f

        // Draw Glass Outline
        val glassPath = Path().apply {
            moveTo(glassLeft, glassTop)
            lineTo(glassLeft + glassWidth * 0.1f, glassTop + glassHeight)
            lineTo(glassLeft + glassWidth * 0.9f, glassTop + glassHeight)
            lineTo(glassLeft + glassWidth, glassTop)
        }
        drawPath(glassPath, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

        // Draw Water Fill with Sine Wave Top
        val fillHeight = glassHeight * animatedProgress
        val waterSurfaceY = (glassTop + glassHeight) - fillHeight

        if (animatedProgress > 0.02f) {
            val wavePath = Path().apply {
                moveTo(glassLeft, glassTop + glassHeight)
                lineTo(glassLeft + glassWidth * 0.1f, glassTop + glassHeight)

                // Bottom right
                lineTo(glassLeft + glassWidth * 0.9f, glassTop + glassHeight)

                // Up right
                val currentTopRightY = (glassTop + glassHeight) - fillHeight
                lineTo(glassLeft + glassWidth * 0.85f, currentTopRightY)

                // Sine wave across top
                val steps = 30
                val stepWidth = (glassWidth * 0.7f) / steps
                for (i in steps downTo 0) {
                    val x = glassLeft + glassWidth * 0.15f + (i * stepWidth)
                    val waveY = waterSurfaceY + (sin(i * 0.3f + waveOffset) * 6.dp.toPx())
                    lineTo(x, waveY)
                }

                close()
            }
            drawPath(wavePath, color = waterColor.copy(alpha = 0.8f))
        }
    }
}

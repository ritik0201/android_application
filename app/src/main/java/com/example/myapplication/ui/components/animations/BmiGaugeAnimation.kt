package com.example.myapplication.ui.components.animations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated semi-circular BMI gauge illustration drawn with Compose Canvas.
 */
@Composable
fun BmiGaugeAnimation(
    bmiValue: Float = 22f,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val normalizedBmi = ((bmiValue - 15f) / (35f - 15f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = normalizedBmi,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "BmiGaugeNeedle"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = 10.dp.toPx()
            val arcSize = this.size.width - strokePx

            // Background Semi-Circle Arc
            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                size = Size(arcSize, arcSize)
            )

            // Progress Semi-Circle Arc
            drawArc(
                color = primaryColor,
                startAngle = 180f,
                sweepAngle = animatedProgress * 180f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize)
            )

            // Needle Pointer
            val needleAngleRad = Math.toRadians((180f + animatedProgress * 180f).toDouble())
            val needleLen = (arcSize / 2f) * 0.7f
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f + (strokePx / 2f)

            val endX = centerX + (cos(needleAngleRad) * needleLen).toFloat()
            val endY = centerY + (sin(needleAngleRad) * needleLen).toFloat()

            drawLine(
                color = primaryColor,
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(color = primaryColor, radius = 6.dp.toPx(), center = Offset(centerX, centerY))
        }
    }
}

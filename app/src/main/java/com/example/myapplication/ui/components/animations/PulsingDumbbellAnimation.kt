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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated pulsing and floating dumbbell illustration drawn with Compose Canvas.
 */
@Composable
fun PulsingDumbbellAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "DumbbellPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DumbbellScale"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DumbbellFloat"
    )

    Canvas(modifier = modifier.size(size)) {
        val width = this.size.width * scale
        val height = this.size.height * scale
        val centerX = this.size.width / 2f
        val centerY = (this.size.height / 2f) + offsetY

        val handleWidth = width * 0.45f
        val handleHeight = height * 0.12f
        val plateWidth = width * 0.12f
        val plateHeight = height * 0.5f

        // Handle
        drawRoundRect(
            color = color,
            topLeft = Offset(centerX - handleWidth / 2f, centerY - handleHeight / 2f),
            size = Size(handleWidth, handleHeight),
            cornerRadius = CornerRadius(handleHeight / 2f)
        )

        // Left Plates
        drawRoundRect(
            color = color,
            topLeft = Offset(centerX - handleWidth / 2f - plateWidth, centerY - plateHeight / 2f),
            size = Size(plateWidth, plateHeight),
            cornerRadius = CornerRadius(6f)
        )

        // Right Plates
        drawRoundRect(
            color = color,
            topLeft = Offset(centerX + handleWidth / 2f, centerY - plateHeight / 2f),
            size = Size(plateWidth, plateHeight),
            cornerRadius = CornerRadius(6f)
        )
    }
}

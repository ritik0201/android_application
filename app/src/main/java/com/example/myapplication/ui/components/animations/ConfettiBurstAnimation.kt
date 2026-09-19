package com.example.myapplication.ui.components.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val angle: Double,
    val speed: Float,
    val radius: Float,
    val color: Color
)

/**
 * Animated confetti burst effect on completion using Compose Canvas.
 */
@Composable
fun ConfettiBurstAnimation(
    modifier: Modifier = Modifier.fillMaxSize(),
    particleCount: Int = 30
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutLinearInEasing)
        )
    }

    val colors = listOf(
        Color(0xFFFF1744), Color(0xFF00E676), Color(0xFF2979FF),
        Color(0xFFFF9100), Color(0xFFAA00FF), Color(0xFFFFEA00)
    )

    val particles = remember {
        List(particleCount) {
            Particle(
                angle = Random.nextDouble(0.0, 2.0 * Math.PI),
                speed = Random.nextFloat() * 250f + 100f,
                radius = Random.nextFloat() * 6f + 4f,
                color = colors.random()
            )
        }
    }

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val p = progress.value

        particles.forEach { particle ->
            val dist = particle.speed * p
            val x = centerX + (cos(particle.angle) * dist).toFloat()
            val y = centerY + (sin(particle.angle) * dist).toFloat()
            val alpha = (1f - p).coerceIn(0f, 1f)

            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.radius,
                center = Offset(x, y)
            )
        }
    }
}

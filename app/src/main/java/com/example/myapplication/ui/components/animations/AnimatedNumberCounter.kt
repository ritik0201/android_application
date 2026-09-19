package com.example.myapplication.ui.components.animations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

/**
 * Reusable animated counter that smoothly counts up to target values when displayed.
 */
@Composable
fun AnimatedNumberCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    suffix: String = ""
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "NumberCounterAnimation"
    )

    Text(
        text = "$animatedValue$suffix",
        modifier = modifier,
        style = style
    )
}

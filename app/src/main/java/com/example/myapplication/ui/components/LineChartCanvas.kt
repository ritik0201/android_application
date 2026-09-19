package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

data class ChartPoint(val label: String, val value: Float)

/**
 * Reusable Line Chart rendered using Compose Canvas (no external libraries needed).
 */
@Composable
fun LineChartCanvas(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    if (points.isEmpty()) {
        Text("Not enough data to render chart.")
        return
    }

    val minVal = (points.minOfOrNull { it.value } ?: 0f) * 0.9f
    val maxVal = (points.maxOfOrNull { it.value } ?: 100f) * 1.1f
    val valRange = if (maxVal == minVal) 1f else (maxVal - minVal)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Plot points & line path
        if (points.size > 1) {
            val path = Path()
            val stepX = width / (points.size - 1)

            points.forEachIndexed { index, point ->
                val x = index * stepX
                val normalizedY = (point.value - minVal) / valRange
                val y = height - (normalizedY * height)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // Draw point circles
                drawCircle(
                    color = lineColor,
                    radius = 6.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            // Draw line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx())
            )
        } else {
            // Single point case
            val x = width / 2
            val y = height / 2
            drawCircle(color = lineColor, radius = 8.dp.toPx(), center = Offset(x, y))
        }
    }
}

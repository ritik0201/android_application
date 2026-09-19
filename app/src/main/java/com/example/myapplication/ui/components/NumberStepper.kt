package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Reusable stepper component replacing raw text input fields.
 */
@Composable
fun NumberStepper(
    value: Double,
    onValueChange: (Double) -> Unit,
    step: Double = 1.0,
    minValue: Double = 0.0,
    maxValue: Double = 500.0,
    label: String = "",
    suffix: String = "",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedButton(
            onClick = {
                val newVal = (value - step).coerceAtLeast(minValue)
                onValueChange(newVal)
            },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.width(36.dp)
        ) {
            Text("-", fontWeight = FontWeight.Bold)
        }

        Text(
            text = if (step % 1.0 == 0.0) "${value.toInt()}$suffix" else "$value$suffix",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedButton(
            onClick = {
                val newVal = (value + step).coerceAtMost(maxValue)
                onValueChange(newVal)
            },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.width(36.dp)
        ) {
            Text("+", fontWeight = FontWeight.Bold)
        }
    }
}

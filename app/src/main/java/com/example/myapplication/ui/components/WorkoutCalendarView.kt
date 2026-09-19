package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

/**
 * Custom Monthly Calendar View highlighting days with completed workouts.
 */
@Composable
fun WorkoutCalendarView(
    workoutDays: Set<LocalDate>,
    yearMonth: YearMonth = YearMonth.now(),
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7
    val dayHeadings = listOf("S", "M", "T", "W", "T", "F", "S")

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            dayHeadings.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalGridSlots = firstDayOfWeek + daysInMonth
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(180.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items((1..totalGridSlots).toList()) { slot ->
                if (slot > firstDayOfWeek) {
                    val dayNum = slot - firstDayOfWeek
                    val date = yearMonth.atDay(dayNum)
                    val isWorkoutDay = workoutDays.contains(date)

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isWorkoutDay) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$dayNum",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isWorkoutDay) FontWeight.Bold else FontWeight.Normal,
                                color = if (isWorkoutDay) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

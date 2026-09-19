package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.entity.BodyMeasurement
import com.example.myapplication.data.entity.UserProfile
import com.example.myapplication.data.entity.WorkoutPlanWithExercises
import com.example.myapplication.data.entity.WorkoutSessionWithSets
import com.example.myapplication.ui.components.WorkoutCalendarView
import com.example.myapplication.ui.components.animations.AnimatedFlameAnimation
import com.example.myapplication.ui.components.animations.AnimatedNumberCounter
import com.example.myapplication.ui.components.animations.PulsingDumbbellAnimation
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/**
 * Enhanced Home dashboard screen displaying profile, today's workout, streak, calendar, calories, water, and weight.
 */
@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    workoutPlans: List<WorkoutPlanWithExercises> = emptyList(),
    workoutSessions: List<WorkoutSessionWithSets> = emptyList(),
    latestMeasurement: BodyMeasurement? = null
) {
    val todayName = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

    val todayPlans = workoutPlans.filter { planWithEx ->
        planWithEx.plan.assignedDays.split(",")
            .map { it.trim() }
            .any { it.equals(todayName, ignoreCase = true) }
    }

    val workoutDates = workoutSessions.map {
        Instant.ofEpochMilli(it.session.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }.toSet()

    val currentStreak = calculateStreak(workoutDates)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (userProfile != null) {
            // Welcome Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Welcome back, ${userProfile.name}! 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Goal: ${userProfile.fitnessGoal}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // Streak & Consistency Card with Animated Flame & Number Counter
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedFlameAnimation(size = 40.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AnimatedNumberCounter(
                                targetValue = currentStreak,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                suffix = " Day Streak!"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Workout Calendar (${LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    WorkoutCalendarView(workoutDays = workoutDates)
                }
            }

            // Today's Planned Workout Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Today's Workout ($todayName)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (todayPlans.isEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PulsingDumbbellAnimation(size = 40.dp)
                            Text(
                                text = "Rest Day / No workout scheduled for $todayName.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        todayPlans.forEach { planWithEx ->
                            Text(
                                text = planWithEx.plan.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (planWithEx.exercises.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(planWithEx.exercises) { ex ->
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("${ex.exerciseName} (${ex.targetSets}x${ex.targetReps})") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Latest Weight & Profile Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val displayWeight = latestMeasurement?.weightKg ?: userProfile.weightKg
                StatCard(title = "Latest Weight", value = "$displayWeight kg", modifier = Modifier.weight(1f))
                StatCard(title = "Height", value = "${userProfile.heightCm} cm", modifier = Modifier.weight(1f))
                StatCard(title = "Age", value = "${userProfile.age} yrs", modifier = Modifier.weight(1f))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

fun calculateStreak(workoutDates: Set<LocalDate>): Int {
    var streak = 0
    var date = LocalDate.now()

    if (!workoutDates.contains(date)) {
        date = date.minusDays(1)
    }

    while (workoutDates.contains(date)) {
        streak++
        date = date.minusDays(1)
    }

    return streak
}

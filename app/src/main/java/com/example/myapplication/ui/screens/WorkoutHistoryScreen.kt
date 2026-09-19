package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.entity.WorkoutSessionWithSets
import com.example.myapplication.viewmodel.WorkoutHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Workout History Screen displaying completed workouts grouped by date.
 */
@Composable
fun WorkoutHistoryScreen(
    viewModel: WorkoutHistoryViewModel = viewModel()
) {
    val sessions by viewModel.workoutSessions.collectAsState()
    var selectedSessionForDetail by remember { mutableStateOf<WorkoutSessionWithSets?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No completed workouts logged yet.\nStart a workout from the Workout Plans tab!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions, key = { it.session.id }) { sessionWithSets ->
                    WorkoutHistoryCard(
                        sessionWithSets = sessionWithSets,
                        onClick = { selectedSessionForDetail = sessionWithSets },
                        onDelete = { viewModel.deleteSession(sessionWithSets.session) }
                    )
                }
            }
        }

        // Detail Dialog
        selectedSessionForDetail?.let { sessionWithSets ->
            WorkoutSessionDetailDialog(
                sessionWithSets = sessionWithSets,
                onDismiss = { selectedSessionForDetail = null },
                onDelete = {
                    viewModel.deleteSession(sessionWithSets.session)
                    selectedSessionForDetail = null
                }
            )
        }
    }
}

@Composable
fun WorkoutHistoryCard(
    sessionWithSets: WorkoutSessionWithSets,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(sessionWithSets.session.timestamp))

    val mins = sessionWithSets.session.durationSeconds / 60
    val secs = sessionWithSets.session.durationSeconds % 60
    val durationFormatted = String.format(Locale.getDefault(), "%02dm %02ds", mins, secs)

    val totalVolume = sessionWithSets.sets.sumOf { it.weightKg * it.reps }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(sessionWithSets.session.planName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(dateString, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SuggestionChip(onClick = {}, label = { Text("Duration: $durationFormatted") })
                SuggestionChip(onClick = {}, label = { Text(String.format(Locale.getDefault(), "Volume: %.1f kg", totalVolume)) })
            }

            if (sessionWithSets.session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Notes: ${sessionWithSets.session.notes}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun WorkoutSessionDetailDialog(
    sessionWithSets: WorkoutSessionWithSets,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(sessionWithSets.session.timestamp))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(sessionWithSets.session.planName, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(dateString, style = MaterialTheme.typography.bodySmall)

                if (sessionWithSets.session.notes.isNotBlank()) {
                    Text("Notes: ${sessionWithSets.session.notes}", style = MaterialTheme.typography.bodyMedium)
                }

                HorizontalDivider()
                Text("Logged Sets:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                LazyColumn(
                    modifier = Modifier.heightIn(max = 250.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sessionWithSets.sets) { set ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(set.exerciseName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Set ${set.setNumber}: ${set.weightKg} kg × ${set.reps} reps", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Delete Log", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

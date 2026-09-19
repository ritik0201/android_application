package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.entity.Exercise
import com.example.myapplication.ui.components.NumberStepper
import com.example.myapplication.viewmodel.ActiveWorkoutViewModel
import java.util.Locale

/**
 * Active Workout logging screen with live timer, stepper inputs, and rest timer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
    availableExercises: List<Exercise>,
    onWorkoutFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showFinishDialog by remember { mutableStateOf(false) }
    var showExercisePicker by remember { mutableStateOf(false) }
    var workoutNotes by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onWorkoutFinished()
        }
    }

    val minutes = uiState.elapsedTimeSeconds / 60
    val seconds = uiState.elapsedTimeSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(uiState.workoutTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Duration: $timeFormatted", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    TextButton(onClick = { showFinishDialog = true }) {
                        Text("Finish", fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { viewModel.cancelWorkout() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Workout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rest Timer Banner
            if (uiState.isRestTimerActive) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Timer, contentDescription = null)
                            Text(
                                "Rest: ${uiState.restSecondsRemaining}s",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            AssistChip(onClick = { viewModel.adjustRestTimer(15) }, label = { Text("+15s") })
                            AssistChip(onClick = { viewModel.adjustRestTimer(-10) }, label = { Text("-10s") })
                            IconButton(onClick = { viewModel.cancelRestTimer() }) {
                                Icon(Icons.Default.Close, contentDescription = "Skip Rest")
                            }
                        }
                    }
                }
            }

            // Exercise List
            if (uiState.exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exercises added yet. Tap 'Add Exercise' below!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.exercises, key = { it.exerciseName }) { exState ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = exState.exerciseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Header row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Set", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                                    Text("Weight (kg)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1.8f))
                                    Text("Reps", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1.5f))
                                    Text("Done", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.8f))
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                // Set rows
                                exState.sets.forEachIndexed { idx, set ->
                                    val currentWeight = set.weightText.toDoubleOrNull() ?: 0.0
                                    val currentReps = set.repsText.toDoubleOrNull() ?: 10.0

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${set.setNumber}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.5f))

                                        NumberStepper(
                                            value = currentWeight,
                                            onValueChange = { viewModel.updateSetWeight(exState.exerciseName, idx, it.toString()) },
                                            step = 2.5,
                                            minValue = 0.0,
                                            maxValue = 400.0,
                                            modifier = Modifier.weight(1.8f)
                                        )

                                        NumberStepper(
                                            value = currentReps,
                                            onValueChange = { viewModel.updateSetReps(exState.exerciseName, idx, it.toInt().toString()) },
                                            step = 1.0,
                                            minValue = 1.0,
                                            maxValue = 100.0,
                                            modifier = Modifier.weight(1.5f)
                                        )

                                        Row(
                                            modifier = Modifier.weight(0.8f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(onClick = { viewModel.toggleSetCompleted(exState.exerciseName, idx) }) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Complete Set",
                                                    tint = if (set.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            if (exState.sets.size > 1) {
                                                IconButton(onClick = { viewModel.removeSet(exState.exerciseName, idx) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Remove Set", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.addSet(exState.exerciseName) }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Set")
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Buttons
            OutlinedButton(
                onClick = { showExercisePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Exercise")
            }
        }

        // Finish Workout Dialog
        if (showFinishDialog) {
            AlertDialog(
                onDismissRequest = { showFinishDialog = false },
                title = { Text("Finish Workout 🎉") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Duration: $timeFormatted")
                        OutlinedTextField(
                            value = workoutNotes,
                            onValueChange = { workoutNotes = it },
                            label = { Text("Workout Notes / Feeling (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout(workoutNotes)
                    }) {
                        Text("Save & Complete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFinishDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Exercise Picker Dialog
        if (showExercisePicker) {
            AlertDialog(
                onDismissRequest = { showExercisePicker = false },
                title = { Text("Select Exercise") },
                text = {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(availableExercises) { exercise ->
                            ElevatedCard(
                                onClick = {
                                    viewModel.addExerciseToWorkout(exercise)
                                    showExercisePicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(exercise.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    SuggestionChip(onClick = {}, label = { Text(exercise.muscleGroup) })
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showExercisePicker = false }) { Text("Close") }
                }
            )
        }
    }
}

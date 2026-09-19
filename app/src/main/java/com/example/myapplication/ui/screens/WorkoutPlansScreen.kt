package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.entity.Exercise
import com.example.myapplication.data.entity.PlanExercise
import com.example.myapplication.data.entity.WorkoutPlan
import com.example.myapplication.data.entity.WorkoutPlanWithExercises
import com.example.myapplication.viewmodel.WorkoutPlanViewModel

/**
 * Workout Plans screen showing list of user routines, detail view, and add/edit plan flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlansScreen(
    viewModel: WorkoutPlanViewModel = viewModel(),
    onStartWorkout: (WorkoutPlanWithExercises) -> Unit = {},
    onStartEmptyWorkout: () -> Unit = {}
) {
    val plans by viewModel.workoutPlans.collectAsState()
    val availableExercises by viewModel.allExercises.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPlanForDetail by remember { mutableStateOf<WorkoutPlanWithExercises?>(null) }
    var planToEdit by remember { mutableStateOf<WorkoutPlanWithExercises?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Workout Plan")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick Free Workout Button
            Button(
                onClick = onStartEmptyWorkout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Quick / Free Workout", fontWeight = FontWeight.Bold)
            }

            if (plans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workout plans created yet.\nTap + to create your first routine!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(plans, key = { it.plan.id }) { planWithExercises ->
                        WorkoutPlanCard(
                            planWithExercises = planWithExercises,
                            onClick = { selectedPlanForDetail = planWithExercises },
                            onStart = { onStartWorkout(planWithExercises) },
                            onEdit = { planToEdit = planWithExercises },
                            onDelete = { viewModel.deleteWorkoutPlan(planWithExercises.plan) }
                        )
                    }
                }
            }
        }

        // Plan Detail Dialog
        selectedPlanForDetail?.let { planWithExercises ->
            WorkoutPlanDetailDialog(
                planWithExercises = planWithExercises,
                onDismiss = { selectedPlanForDetail = null }
            )
        }

        // Add / Edit Workout Plan Dialog
        if (showAddDialog || planToEdit != null) {
            AddEditWorkoutPlanDialog(
                planToEdit = planToEdit,
                availableExercises = availableExercises,
                onDismiss = {
                    showAddDialog = false
                    planToEdit = null
                },
                onSave = { plan, exercises ->
                    viewModel.saveWorkoutPlan(plan, exercises)
                    showAddDialog = false
                    planToEdit = null
                }
            )
        }
    }
}

@Composable
fun WorkoutPlanCard(
    planWithExercises: WorkoutPlanWithExercises,
    onClick: () -> Unit,
    onStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                Text(
                    text = planWithExercises.plan.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Routine")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Routine", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (planWithExercises.plan.description.isNotBlank()) {
                Text(
                    text = planWithExercises.plan.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (planWithExercises.plan.assignedDays.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    planWithExercises.plan.assignedDays.split(",").forEach { day ->
                        if (day.trim().isNotEmpty()) {
                            SuggestionChip(onClick = {}, label = { Text(day.trim()) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${planWithExercises.exercises.size} Exercises",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                FilledTonalButton(onClick = onStart) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Workout")
                }
            }
        }
    }
}

@Composable
fun WorkoutPlanDetailDialog(
    planWithExercises: WorkoutPlanWithExercises,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(planWithExercises.plan.title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (planWithExercises.plan.description.isNotBlank()) {
                    Text(planWithExercises.plan.description, style = MaterialTheme.typography.bodyMedium)
                }

                if (planWithExercises.plan.assignedDays.isNotBlank()) {
                    Text("Days: ${planWithExercises.plan.assignedDays}", style = MaterialTheme.typography.titleSmall)
                }

                Text("Planned Exercises:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                if (planWithExercises.exercises.isEmpty()) {
                    Text("No exercises added to this plan.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(planWithExercises.exercises) { ex ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ex.exerciseName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Text("${ex.targetSets} sets × ${ex.targetReps} reps", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWorkoutPlanDialog(
    planToEdit: WorkoutPlanWithExercises?,
    availableExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSave: (WorkoutPlan, List<PlanExercise>) -> Unit
) {
    var title by remember { mutableStateOf(planToEdit?.plan?.title ?: "") }
    var description by remember { mutableStateOf(planToEdit?.plan?.description ?: "") }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDays by remember {
        mutableStateOf(
            planToEdit?.plan?.assignedDays?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
        )
    }

    var selectedPlanExercises by remember {
        mutableStateOf(planToEdit?.exercises ?: emptyList())
    }

    var showExercisePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (planToEdit == null) "Create Workout Plan" else "Edit Workout Plan") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Title (e.g., Push Day)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Assign to Days:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(daysOfWeek) { day ->
                        FilterChip(
                            selected = selectedDays.contains(day),
                            onClick = {
                                selectedDays = if (selectedDays.contains(day)) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            },
                            label = { Text(day.take(3)) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Exercises (${selectedPlanExercises.size}):", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { showExercisePicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Exercise")
                    }
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(selectedPlanExercises) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.exerciseName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("${item.targetSets} sets × ${item.targetReps} reps", style = MaterialTheme.typography.bodySmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        if (item.targetSets > 1) {
                                            selectedPlanExercises = selectedPlanExercises.map {
                                                if (it == item) it.copy(targetSets = it.targetSets - 1) else it
                                            }
                                        }
                                    }) { Text("-S", fontWeight = FontWeight.Bold) }

                                    IconButton(onClick = {
                                        selectedPlanExercises = selectedPlanExercises.map {
                                            if (it == item) it.copy(targetSets = it.targetSets + 1) else it
                                        }
                                    }) { Text("+S", fontWeight = FontWeight.Bold) }

                                    IconButton(onClick = {
                                        selectedPlanExercises = selectedPlanExercises - item
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                errorMessage?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter routine title"
                    } else if (selectedPlanExercises.isEmpty()) {
                        errorMessage = "Please add at least one exercise"
                    } else {
                        val plan = WorkoutPlan(
                            id = planToEdit?.plan?.id ?: 0,
                            title = title.trim(),
                            description = description.trim(),
                            assignedDays = selectedDays.joinToString(", ")
                        )
                        onSave(plan, selectedPlanExercises)
                    }
                }
            ) {
                Text("Save Routine")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

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
                                selectedPlanExercises = selectedPlanExercises + PlanExercise(
                                    exerciseId = exercise.id,
                                    exerciseName = exercise.name,
                                    targetSets = 3,
                                    targetReps = 10
                                )
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

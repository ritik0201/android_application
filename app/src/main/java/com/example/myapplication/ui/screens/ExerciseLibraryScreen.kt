package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.entity.Exercise
import com.example.myapplication.viewmodel.ExerciseViewModel

/**
 * Exercise Library screen supporting search, muscle group filtering, and custom exercise CRUD.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    viewModel: ExerciseViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMuscleGroup by viewModel.selectedMuscleGroup.collectAsState()
    val exercises by viewModel.filteredExercises.collectAsState()

    var selectedExerciseForDetail by remember { mutableStateOf<Exercise?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<Exercise?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Exercise")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search exercise or muscle...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Muscle Group Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(viewModel.muscleGroups) { muscle ->
                    FilterChip(
                        selected = selectedMuscleGroup == muscle,
                        onClick = { viewModel.onMuscleGroupSelect(muscle) },
                        label = { Text(muscle) }
                    )
                }
            }

            // Exercise List or Empty State
            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No exercises found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(exercises, key = { it.id }) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { selectedExerciseForDetail = exercise }
                        )
                    }
                }
            }
        }

        // Exercise Detail Dialog
        selectedExerciseForDetail?.let { exercise ->
            ExerciseDetailDialog(
                exercise = exercise,
                onDismiss = { selectedExerciseForDetail = null },
                onEdit = {
                    selectedExerciseForDetail = null
                    exerciseToEdit = exercise
                },
                onDelete = {
                    viewModel.deleteCustomExercise(exercise)
                    selectedExerciseForDetail = null
                }
            )
        }

        // Add / Edit Custom Exercise Dialog
        if (showAddDialog || exerciseToEdit != null) {
            AddEditExerciseDialog(
                exerciseToEdit = exerciseToEdit,
                muscleGroups = viewModel.muscleGroups.filter { it != "All" },
                onDismiss = {
                    showAddDialog = false
                    exerciseToEdit = null
                },
                onSave = { name, muscle, equipment, instructions ->
                    if (exerciseToEdit == null) {
                        viewModel.addCustomExercise(name, muscle, equipment, instructions)
                    } else {
                        exerciseToEdit?.let { existing ->
                            viewModel.updateCustomExercise(
                                existing.copy(
                                    name = name,
                                    muscleGroup = muscle,
                                    equipment = equipment,
                                    instructions = instructions
                                )
                            )
                        }
                    }
                    showAddDialog = false
                    exerciseToEdit = null
                }
            )
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (exercise.isCustom) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Custom", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(exercise.muscleGroup) }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(exercise.equipment) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(exercise.name, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(onClick = {}, label = { Text("Target: ${exercise.muscleGroup}") })
                    SuggestionChip(onClick = {}, label = { Text("Equipment: ${exercise.equipment}") })
                }
                Text("Instructions:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(exercise.instructions, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            if (exercise.isCustom) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            } else {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        },
        dismissButton = {
            if (exercise.isCustom) {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        }
    )
}

@Composable
fun AddEditExerciseDialog(
    exerciseToEdit: Exercise?,
    muscleGroups: List<String>,
    onDismiss: () -> Unit,
    onSave: (name: String, muscleGroup: String, equipment: String, instructions: String) -> Unit
) {
    var name by remember { mutableStateOf(exerciseToEdit?.name ?: "") }
    var selectedMuscle by remember { mutableStateOf(exerciseToEdit?.muscleGroup ?: muscleGroups.firstOrNull() ?: "Chest") }
    var equipment by remember { mutableStateOf(exerciseToEdit?.equipment ?: "") }
    var instructions by remember { mutableStateOf(exerciseToEdit?.instructions ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exerciseToEdit == null) "Add Custom Exercise" else "Edit Exercise") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Muscle Group:", style = MaterialTheme.typography.bodyMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(muscleGroups) { muscle ->
                        FilterChip(
                            selected = selectedMuscle == muscle,
                            onClick = { selectedMuscle = muscle },
                            label = { Text(muscle) }
                        )
                    }
                }

                OutlinedTextField(
                    value = equipment,
                    onValueChange = { equipment = it },
                    label = { Text("Equipment (e.g. Dumbbell, Barbell)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter exercise name"
                    } else {
                        onSave(name, selectedMuscle, equipment, instructions)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

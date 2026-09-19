package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.ChartPoint
import com.example.myapplication.ui.components.LineChartCanvas
import com.example.myapplication.viewmodel.ProgressViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weight & Body", "PRs", "Photos", "Badges 🏆")

    val measurements by viewModel.bodyMeasurements.collectAsState()
    val records by viewModel.personalRecords.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Log Measurement")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    // Body Weight & Measurements Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Weight Trend (kg)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                val chartPoints = measurements.map {
                                    val dateStr = SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(it.timestamp))
                                    ChartPoint(dateStr, it.weightKg.toFloat())
                                }

                                LineChartCanvas(points = chartPoints)
                            }
                        }

                        Text("Measurement History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        if (measurements.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No measurements logged yet. Tap + to add!")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(measurements.reversed()) { item ->
                                    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(item.timestamp))
                                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                                Text("${item.weightKg} kg", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                                Text("Chest: ${item.chestCm}cm | Waist: ${item.waistCm}cm | Arms: ${item.armsCm}cm | Thighs: ${item.thighsCm}cm", style = MaterialTheme.typography.bodySmall)
                                            }
                                            IconButton(onClick = { viewModel.deleteMeasurement(item) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Personal Records & Strength Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("All-Time Personal Records (PRs)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        if (records.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No personal records calculated yet.\nComplete workouts to automatically track PRs!")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(records) { pr ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(pr.exerciseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "${pr.maxWeightKg} kg × ${pr.repsAtMax} reps",
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> ProgressPhotosScreen()
                3 -> AchievementsScreen()
            }
        }

        if (showAddDialog) {
            AddMeasurementDialog(
                onDismiss = { showAddDialog = false },
                onSave = { weight, chest, waist, arms, thighs ->
                    viewModel.addMeasurement(weight, chest, waist, arms, thighs)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddMeasurementDialog(
    onDismiss: () -> Unit,
    onSave: (Double, Double, Double, Double, Double) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var chestText by remember { mutableStateOf("") }
    var waistText by remember { mutableStateOf("") }
    var armsText by remember { mutableStateOf("") }
    var thighsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Body Measurements") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chestText,
                        onValueChange = { chestText = it },
                        label = { Text("Chest (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = waistText,
                        onValueChange = { waistText = it },
                        label = { Text("Waist (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = armsText,
                        onValueChange = { armsText = it },
                        label = { Text("Arms (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = thighsText,
                        onValueChange = { thighsText = it },
                        label = { Text("Thighs (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightText.toDoubleOrNull() ?: 0.0
                    val c = chestText.toDoubleOrNull() ?: 0.0
                    val waist = waistText.toDoubleOrNull() ?: 0.0
                    val a = armsText.toDoubleOrNull() ?: 0.0
                    val t = thighsText.toDoubleOrNull() ?: 0.0
                    onSave(w, c, waist, a, t)
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.animations.AnimatedProgressRing
import com.example.myapplication.ui.components.animations.WaterGlassWaveAnimation
import com.example.myapplication.viewmodel.NutritionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = viewModel()
) {
    val mealLogs by viewModel.mealLogs.collectAsState()
    val todayWaterLog by viewModel.todayWaterLog.collectAsState()

    var showAddMealDialog by remember { mutableStateOf(false) }

    val totalCalories = mealLogs.sumOf { it.calories }
    val totalProtein = mealLogs.sumOf { it.proteinGrams }
    val totalCarbs = mealLogs.sumOf { it.carbsGrams }
    val totalFat = mealLogs.sumOf { it.fatGrams }

    val waterMl = todayWaterLog?.amountMl ?: 0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddMealDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Log Meal")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calorie & Macro Summary Card with Animated Progress Ring
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Restaurant, contentDescription = null)
                                Text("Daily Calories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$totalCalories / ${viewModel.dailyCalorieGoal} kcal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("P: ${totalProtein.toInt()}g | C: ${totalCarbs.toInt()}g | F: ${totalFat.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                        }

                        val calProgress = (totalCalories.toFloat() / viewModel.dailyCalorieGoal).coerceIn(0f, 1f)
                        AnimatedProgressRing(
                            progress = calProgress,
                            size = 80.dp,
                            strokeWidth = 10.dp
                        )
                    }
                }
            }

            // Water Tracker Card with Animated Wave Glass
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Water Intake", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$waterMl / ${viewModel.dailyWaterGoalMl} ml", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }

                            val waterProgress = (waterMl.toFloat() / viewModel.dailyWaterGoalMl).coerceIn(0f, 1f)
                            WaterGlassWaveAnimation(
                                progress = waterProgress,
                                size = 60.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addWater(250) },
                                modifier = Modifier.weight(1f)
                            ) { Text("+250 ml") }

                            Button(
                                onClick = { viewModel.addWater(500) },
                                modifier = Modifier.weight(1f)
                            ) { Text("+500 ml") }

                            OutlinedButton(onClick = { viewModel.resetWater() }) {
                                Text("Reset")
                            }
                        }
                    }
                }
            }

            // Meal Log List
            item {
                Text("Today's Meals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            if (mealLogs.isEmpty()) {
                item {
                    Text("No meals logged today. Tap + to add a meal!", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(mealLogs, key = { it.id }) { meal ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(meal.mealName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${meal.mealType} • ${meal.calories} kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                Text("P: ${meal.proteinGrams.toInt()}g | C: ${meal.carbsGrams.toInt()}g | F: ${meal.fatGrams.toInt()}g", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.deleteMealLog(meal) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Meal", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        if (showAddMealDialog) {
            AddMealDialog(
                onDismiss = { showAddMealDialog = false },
                onSave = { name, type, cals, p, c, f ->
                    viewModel.addMealLog(name, type, cals, p, c, f)
                    showAddMealDialog = false
                }
            )
        }
    }
}

@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Double, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Lunch") }
    var calText by remember { mutableStateOf("") }
    var pText by remember { mutableStateOf("") }
    var cText by remember { mutableStateOf("") }
    var fText by remember { mutableStateOf("") }

    val types = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Meal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal Name (e.g. Grilled Chicken)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    types.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.take(4)) }
                        )
                    }
                }

                OutlinedTextField(
                    value = calText,
                    onValueChange = { calText = it },
                    label = { Text("Calories (kcal)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pText,
                        onValueChange = { pText = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cText,
                        onValueChange = { cText = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fText,
                        onValueChange = { fText = it },
                        label = { Text("Fat (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val cals = calText.toIntOrNull() ?: 0
                        val p = pText.toDoubleOrNull() ?: 0.0
                        val c = cText.toDoubleOrNull() ?: 0.0
                        val f = fText.toDoubleOrNull() ?: 0.0
                        onSave(name, selectedType, cals, p, c, f)
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

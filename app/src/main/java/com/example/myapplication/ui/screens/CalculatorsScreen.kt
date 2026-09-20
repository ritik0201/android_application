package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.animations.AnimatedFlameAnimation
import com.example.myapplication.ui.components.animations.BmiGaugeAnimation
import com.example.myapplication.ui.components.animations.ConfettiBurstAnimation
import com.example.myapplication.ui.components.animations.PulsingDumbbellAnimation
import com.example.myapplication.viewmodel.CalculatorsViewModel

@Composable
fun CalculatorsScreen(
    viewModel: CalculatorsViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("BMI", "TDEE / Calories", "One-Rep Max (1RM)")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> BmiCalculatorTab(viewModel)
            1 -> TdeeCalculatorTab(viewModel)
            2 -> OneRmCalculatorTab(viewModel)
        }
    }
}

@Composable
fun BmiCalculatorTab(viewModel: CalculatorsViewModel) {
    var heightText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    val result by viewModel.bmiResult.collectAsState()

    val parsedBmi = result?.substringAfter("BMI: ")?.substringBefore(" ")?.toFloatOrNull() ?: 22f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BmiGaugeAnimation(bmiValue = parsedBmi, size = 110.dp)

        Text("Body Mass Index (BMI) Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = heightText, onValueChange = { heightText = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = weightText, onValueChange = { weightText = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = { viewModel.calculateBmi(heightText, weightText) }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate BMI")
        }

        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
fun TdeeCalculatorTab(viewModel: CalculatorsViewModel) {
    var ageText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var heightText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }

    val activityLevels = listOf(
        "Sedentary" to 1.2,
        "Lightly Active" to 1.375,
        "Moderately Active" to 1.55,
        "Very Active" to 1.725
    )
    var selectedActivity by remember { mutableStateOf(activityLevels.first()) }
    val result by viewModel.tdeeResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedFlameAnimation(size = 60.dp)

        Text("Daily Calorie Needs (TDEE) Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = ageText, onValueChange = { ageText = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = gender == "Male", onClick = { gender = "Male" }, label = { Text("Male") }, modifier = Modifier.weight(1f))
            FilterChip(selected = gender == "Female", onClick = { gender = "Female" }, label = { Text("Female") }, modifier = Modifier.weight(1f))
        }
        OutlinedTextField(value = heightText, onValueChange = { heightText = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = weightText, onValueChange = { weightText = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())

        Text("Activity Level:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxWidth()) {
            activityLevels.forEach { act ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedActivity == act, onClick = { selectedActivity = act })
                    Text(act.first)
                }
            }
        }

        Button(
            onClick = { viewModel.calculateTdee(ageText, gender, heightText, weightText, selectedActivity.second) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate TDEE")
        }

        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiaryContainer)
            }
        }
    }
}

@Composable
fun OneRmCalculatorTab(viewModel: CalculatorsViewModel) {
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    val result by viewModel.oneRmResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            PulsingDumbbellAnimation(size = 80.dp)
            if (result != null) {
                ConfettiBurstAnimation(modifier = Modifier.size(150.dp))
            }
        }

        Text("One-Rep Max (1RM) Strength Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = weightText, onValueChange = { weightText = it }, label = { Text("Weight Lifted (kg)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = repsText, onValueChange = { repsText = it }, label = { Text("Reps Performed") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = { viewModel.calculateOneRepMax(weightText, repsText) }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate 1RM")
        }

        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

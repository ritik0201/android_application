package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.ActiveWorkoutViewModel
import com.example.myapplication.viewmodel.WorkoutPlanViewModel

@Composable
fun WorkoutsScreen(
    activeWorkoutViewModel: ActiveWorkoutViewModel = viewModel(),
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
) {
    val activeState by activeWorkoutViewModel.uiState.collectAsState()
    val availableExercises by workoutPlanViewModel.allExercises.collectAsState()

    if (activeState.isActive) {
        ActiveWorkoutScreen(
            viewModel = activeWorkoutViewModel,
            availableExercises = availableExercises,
            onWorkoutFinished = {
                // Returns automatically to tabs view when workout finishes
            }
        )
    } else {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = listOf("Workout Plans", "Workout History", "Exercise Library")

        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> WorkoutPlansScreen(
                    onStartWorkout = { planWithEx ->
                        activeWorkoutViewModel.startWorkoutFromPlan(planWithEx)
                    },
                    onStartEmptyWorkout = {
                        activeWorkoutViewModel.startEmptyWorkout()
                    }
                )
                1 -> WorkoutHistoryScreen()
                2 -> ExerciseLibraryScreen()
            }
        }
    }
}

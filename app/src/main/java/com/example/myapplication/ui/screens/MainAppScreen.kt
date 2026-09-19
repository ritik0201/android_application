package com.example.myapplication.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.entity.UserProfile
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.theme.Motion
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.viewmodel.ProgressViewModel

/**
 * Main application layout container with Bottom Navigation and animated NavHost transitions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    userProfile: UserProfile?,
    mainViewModel: MainViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel()
) {
    val navController = rememberNavController()
    val workoutPlans by mainViewModel.workoutPlans.collectAsState()
    val workoutSessions by mainViewModel.workoutSessions.collectAsState()
    val bodyMeasurements by progressViewModel.bodyMeasurements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FitTrack") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(Motion.DURATION_MEDIUM, easing = Motion.EASING_STANDARD)) +
                        slideInHorizontally(animationSpec = tween(Motion.DURATION_MEDIUM, easing = Motion.EASING_STANDARD))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(Motion.DURATION_MEDIUM, easing = Motion.EASING_STANDARD)) +
                        slideOutHorizontally(animationSpec = tween(Motion.DURATION_MEDIUM, easing = Motion.EASING_STANDARD))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    userProfile = userProfile,
                    workoutPlans = workoutPlans,
                    workoutSessions = workoutSessions,
                    latestMeasurement = bodyMeasurements.lastOrNull()
                )
            }
            composable(Screen.Workouts.route) { WorkoutsScreen() }
            composable(Screen.Progress.route) { ProgressScreen() }
            composable(Screen.Nutrition.route) { NutritionScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

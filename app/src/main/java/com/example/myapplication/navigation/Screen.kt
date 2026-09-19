package com.example.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing screens in the app navigation.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Onboarding")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Workouts : Screen("workouts", "Workouts", Icons.Default.FitnessCenter)
    object Progress : Screen("progress", "Progress", Icons.Default.ShowChart)
    object Nutrition : Screen("nutrition", "Nutrition", Icons.Default.Restaurant)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

// List of screens shown in the bottom navigation bar
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Workouts,
    Screen.Progress,
    Screen.Nutrition,
    Screen.Settings
)

package com.example.myapplication.util

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

/**
 * DataStore preferences manager for app settings (units, theme, goals, reminders).
 */
class UserPreferences(private val context: Context) {

    companion object {
        private val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        private val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        private val APP_THEME = stringPreferencesKey("app_theme")
        private val CALORIE_GOAL = intPreferencesKey("calorie_goal")
        private val WATER_GOAL_ML = intPreferencesKey("water_goal_ml")
        private val WORKOUT_REMINDER_ENABLED = booleanPreferencesKey("workout_reminder_enabled")
        private val WATER_REMINDER_ENABLED = booleanPreferencesKey("water_reminder_enabled")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[IS_ONBOARDING_COMPLETED] ?: false }
    val weightUnit: Flow<String> = context.dataStore.data.map { it[WEIGHT_UNIT] ?: "kg" }
    val appTheme: Flow<String> = context.dataStore.data.map { it[APP_THEME] ?: "system" }
    val calorieGoal: Flow<Int> = context.dataStore.data.map { it[CALORIE_GOAL] ?: 2000 }
    val waterGoalMl: Flow<Int> = context.dataStore.data.map { it[WATER_GOAL_ML] ?: 2500 }
    val workoutReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[WORKOUT_REMINDER_ENABLED] ?: false }
    val waterReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[WATER_REMINDER_ENABLED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[IS_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setWeightUnit(unit: String) {
        context.dataStore.edit { it[WEIGHT_UNIT] = unit }
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { it[APP_THEME] = theme }
    }

    suspend fun setCalorieGoal(goal: Int) {
        context.dataStore.edit { it[CALORIE_GOAL] = goal }
    }

    suspend fun setWaterGoalMl(goal: Int) {
        context.dataStore.edit { it[WATER_GOAL_ML] = goal }
    }

    suspend fun setWorkoutReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[WORKOUT_REMINDER_ENABLED] = enabled }
    }

    suspend fun setWaterReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[WATER_REMINDER_ENABLED] = enabled }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.WorkoutSessionWithSets
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import com.example.myapplication.worker.WaterReminderWorker
import com.example.myapplication.worker.WorkoutReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val repository: FitTrackRepository
    private val workManager = WorkManager.getInstance(application)

    val weightUnit: StateFlow<String>
    val appTheme: StateFlow<String>
    val calorieGoal: StateFlow<Int>
    val waterGoalMl: StateFlow<Int>
    val workoutReminderEnabled: StateFlow<Boolean>
    val waterReminderEnabled: StateFlow<Boolean>
    val workoutSessions: StateFlow<List<WorkoutSessionWithSets>>

    init {
        val database = FitTrackDatabase.getDatabase(application)
        repository = FitTrackRepository(
            database.userProfileDao(),
            database.exerciseDao(),
            database.workoutPlanDao(),
            database.workoutSessionDao(),
            database.bodyMeasurementDao(),
            database.nutritionDao(),
            database.achievementDao(),
            database.progressPhotoDao(),
            database.exerciseNoteDao(),
            userPreferences
        )

        weightUnit = userPreferences.weightUnit.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "kg")
        appTheme = userPreferences.appTheme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
        calorieGoal = userPreferences.calorieGoal.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)
        waterGoalMl = userPreferences.waterGoalMl.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2500)
        workoutReminderEnabled = userPreferences.workoutReminderEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
        waterReminderEnabled = userPreferences.waterReminderEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
        workoutSessions = repository.workoutSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setWeightUnit(unit: String) {
        viewModelScope.launch { userPreferences.setWeightUnit(unit) }
    }

    fun setAppTheme(theme: String) {
        viewModelScope.launch { userPreferences.setAppTheme(theme) }
    }

    fun setCalorieGoal(goal: Int) {
        viewModelScope.launch { userPreferences.setCalorieGoal(goal) }
    }

    fun setWaterGoalMl(goal: Int) {
        viewModelScope.launch { userPreferences.setWaterGoalMl(goal) }
    }

    fun toggleWorkoutReminder(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setWorkoutReminderEnabled(enabled)
            if (enabled) {
                val request = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(24, TimeUnit.HOURS).build()
                workManager.enqueue(request)
            } else {
                workManager.cancelAllWorkByTag(WorkoutReminderWorker::class.java.name)
            }
        }
    }

    fun toggleWaterReminder(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setWaterReminderEnabled(enabled)
            if (enabled) {
                val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(4, TimeUnit.HOURS).build()
                workManager.enqueue(request)
            } else {
                workManager.cancelAllWorkByTag(WaterReminderWorker::class.java.name)
            }
        }
    }

    fun generateWorkoutCsv(): String {
        val sessions = workoutSessions.value
        val csvBuilder = StringBuilder()
        csvBuilder.append("Session ID,Routine,Date,Duration (s),Exercise,Set #,Weight (kg),Reps\n")

        sessions.forEach { sessionWithSets ->
            val s = sessionWithSets.session
            sessionWithSets.sets.forEach { set ->
                csvBuilder.append("${s.id},\"${s.planName}\",${s.timestamp},${s.durationSeconds},\"${set.exerciseName}\",${set.setNumber},${set.weightKg},${set.reps}\n")
            }
        }
        return csvBuilder.toString()
    }

    fun generateFullJsonBackup(): String {
        var json = ""
        viewModelScope.launch {
            json = repository.exportJsonBackup()
        }
        return json
    }

    fun restoreFullJsonBackup(jsonString: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.restoreJsonBackup(jsonString)
            onComplete(result)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}

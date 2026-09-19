package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.MealLog
import com.example.myapplication.data.entity.WaterLog
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    val mealLogs: StateFlow<List<MealLog>>
    val todayWaterLog: StateFlow<WaterLog?>

    val dailyCalorieGoal = 2000
    val dailyWaterGoalMl = 2500

    val todayDateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        val database = FitTrackDatabase.getDatabase(application)
        val userPreferences = UserPreferences(application)
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

        mealLogs = repository.mealLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        todayWaterLog = repository.getWaterLogForDate(todayDateString).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun addMealLog(name: String, type: String, cals: Int, protein: Double, carbs: Double, fat: Double) {
        viewModelScope.launch {
            repository.insertMealLog(
                MealLog(
                    mealName = name,
                    mealType = type,
                    calories = cals,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatGrams = fat
                )
            )
        }
    }

    fun deleteMealLog(mealLog: MealLog) {
        viewModelScope.launch {
            repository.deleteMealLog(mealLog)
        }
    }

    fun addWater(amountMl: Int) {
        val current = todayWaterLog.value?.amountMl ?: 0
        val updated = WaterLog(
            id = todayWaterLog.value?.id ?: 0,
            dateString = todayDateString,
            amountMl = current + amountMl
        )
        viewModelScope.launch {
            repository.insertOrUpdateWaterLog(updated)
        }
    }

    fun resetWater() {
        val updated = WaterLog(
            id = todayWaterLog.value?.id ?: 0,
            dateString = todayDateString,
            amountMl = 0
        )
        viewModelScope.launch {
            repository.insertOrUpdateWaterLog(updated)
        }
    }
}

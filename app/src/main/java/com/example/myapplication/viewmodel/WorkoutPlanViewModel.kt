package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.Exercise
import com.example.myapplication.data.entity.PlanExercise
import com.example.myapplication.data.entity.WorkoutPlan
import com.example.myapplication.data.entity.WorkoutPlanWithExercises
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    val workoutPlans: StateFlow<List<WorkoutPlanWithExercises>>
    val allExercises: StateFlow<List<Exercise>>

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

        workoutPlans = repository.workoutPlans.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allExercises = repository.exercises.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun saveWorkoutPlan(plan: WorkoutPlan, exercises: List<PlanExercise>) {
        if (plan.title.isBlank()) return
        viewModelScope.launch {
            repository.saveWorkoutPlan(plan, exercises)
        }
    }

    fun deleteWorkoutPlan(plan: WorkoutPlan) {
        viewModelScope.launch {
            repository.deleteWorkoutPlan(plan)
        }
    }
}

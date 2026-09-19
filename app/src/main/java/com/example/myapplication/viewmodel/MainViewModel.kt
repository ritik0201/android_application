package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.UserProfile
import com.example.myapplication.data.entity.WorkoutPlanWithExercises
import com.example.myapplication.data.entity.WorkoutSessionWithSets
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    val isOnboardingCompleted: StateFlow<Boolean?>
    val userProfile: StateFlow<UserProfile?>
    val workoutPlans: StateFlow<List<WorkoutPlanWithExercises>>
    val workoutSessions: StateFlow<List<WorkoutSessionWithSets>>

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

        viewModelScope.launch {
            repository.seedExercisesIfEmpty()
        }

        isOnboardingCompleted = repository.isOnboardingCompleted.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        workoutPlans = repository.workoutPlans.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        workoutSessions = repository.workoutSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}

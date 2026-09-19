package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.UserProfile
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val name: String = "",
    val age: String = "",
    val gender: String = "Male",
    val heightCm: String = "",
    val weightKg: String = "",
    val goal: String = "Stay Fit",
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

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
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }

    fun onAgeChange(value: String) {
        _uiState.value = _uiState.value.copy(age = value, errorMessage = null)
    }

    fun onGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(gender = value)
    }

    fun onHeightChange(value: String) {
        _uiState.value = _uiState.value.copy(heightCm = value, errorMessage = null)
    }

    fun onWeightChange(value: String) {
        _uiState.value = _uiState.value.copy(weightKg = value, errorMessage = null)
    }

    fun onGoalChange(value: String) {
        _uiState.value = _uiState.value.copy(goal = value)
    }

    fun saveProfile() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter your name")
            return
        }

        val ageInt = state.age.toIntOrNull()
        if (ageInt == null || ageInt <= 0) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid age")
            return
        }

        val heightDouble = state.heightCm.toDoubleOrNull()
        if (heightDouble == null || heightDouble <= 0) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid height in cm")
            return
        }

        val weightDouble = state.weightKg.toDoubleOrNull()
        if (weightDouble == null || weightDouble <= 0) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid weight in kg")
            return
        }

        viewModelScope.launch {
            val profile = UserProfile(
                name = state.name.trim(),
                age = ageInt,
                gender = state.gender,
                heightCm = heightDouble,
                weightKg = weightDouble,
                fitnessGoal = state.goal
            )
            repository.saveUserProfile(profile)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}

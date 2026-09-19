package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.Exercise
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow("All")
    val selectedMuscleGroup: StateFlow<String> = _selectedMuscleGroup.asStateFlow()

    val muscleGroups = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body")

    val filteredExercises: StateFlow<List<Exercise>>

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

        filteredExercises = combine(
            repository.exercises,
            _searchQuery,
            _selectedMuscleGroup
        ) { exercises, query, muscle ->
            exercises.filter { exercise ->
                val matchesQuery = exercise.name.contains(query, ignoreCase = true) ||
                        exercise.muscleGroup.contains(query, ignoreCase = true) ||
                        exercise.equipment.contains(query, ignoreCase = true)
                val matchesMuscle = muscle == "All" || exercise.muscleGroup.equals(muscle, ignoreCase = true)
                matchesQuery && matchesMuscle
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onMuscleGroupSelect(muscle: String) {
        _selectedMuscleGroup.value = muscle
    }

    fun addCustomExercise(name: String, muscleGroup: String, equipment: String, instructions: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val customExercise = Exercise(
                name = name.trim(),
                muscleGroup = muscleGroup,
                equipment = equipment.ifBlank { "Bodyweight" },
                instructions = instructions.ifBlank { "No instructions provided." },
                isCustom = true
            )
            repository.insertExercise(customExercise)
        }
    }

    fun updateCustomExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }

    fun deleteCustomExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }
}

package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.BodyMeasurement
import com.example.myapplication.data.entity.WorkoutSessionWithSets
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PersonalRecord(
    val exerciseName: String,
    val maxWeightKg: Double,
    val repsAtMax: Int
)

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    val bodyMeasurements: StateFlow<List<BodyMeasurement>>
    val personalRecords: StateFlow<List<PersonalRecord>>
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

        bodyMeasurements = repository.bodyMeasurements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        workoutSessions = repository.workoutSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        personalRecords = repository.workoutSessions.map { sessions ->
            val recordsMap = mutableMapOf<String, PersonalRecord>()
            sessions.forEach { sessionWithSets ->
                sessionWithSets.sets.forEach { set ->
                    val currentMax = recordsMap[set.exerciseName]?.maxWeightKg ?: 0.0
                    if (set.weightKg >= currentMax && set.weightKg > 0) {
                        recordsMap[set.exerciseName] = PersonalRecord(
                            exerciseName = set.exerciseName,
                            maxWeightKg = set.weightKg,
                            repsAtMax = set.reps
                        )
                    }
                }
            }
            recordsMap.values.sortedByDescending { it.maxWeightKg }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addMeasurement(weightKg: Double, chestCm: Double, waistCm: Double, armsCm: Double, thighsCm: Double) {
        viewModelScope.launch {
            repository.insertMeasurement(
                BodyMeasurement(
                    weightKg = weightKg,
                    chestCm = chestCm,
                    waistCm = waistCm,
                    armsCm = armsCm,
                    thighsCm = thighsCm
                )
            )
        }
    }

    fun deleteMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            repository.deleteMeasurement(measurement)
        }
    }
}

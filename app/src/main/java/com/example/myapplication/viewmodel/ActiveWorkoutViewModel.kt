package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.Exercise
import com.example.myapplication.data.entity.LoggedSet
import com.example.myapplication.data.entity.WorkoutPlanWithExercises
import com.example.myapplication.data.entity.WorkoutSession
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActiveSetState(
    val setNumber: Int,
    val weightText: String = "0.0",
    val repsText: String = "10",
    val isCompleted: Boolean = false
)

data class ActiveExerciseState(
    val exerciseName: String,
    val sets: List<ActiveSetState>
)

data class ActiveWorkoutUiState(
    val isActive: Boolean = false,
    val workoutTitle: String = "",
    val elapsedTimeSeconds: Long = 0,
    val exercises: List<ActiveExerciseState> = emptyList(),
    val isRestTimerActive: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val isFinished: Boolean = false
)

class ActiveWorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

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

    fun startWorkoutFromPlan(planWithExercises: WorkoutPlanWithExercises) {
        val activeExercises = planWithExercises.exercises.map { planEx ->
            val sets = (1..planEx.targetSets).map { setNum ->
                ActiveSetState(
                    setNumber = setNum,
                    weightText = "0.0",
                    repsText = planEx.targetReps.toString(),
                    isCompleted = false
                )
            }
            ActiveExerciseState(exerciseName = planEx.exerciseName, sets = sets)
        }

        _uiState.value = ActiveWorkoutUiState(
            isActive = true,
            workoutTitle = planWithExercises.plan.title,
            elapsedTimeSeconds = 0,
            exercises = activeExercises
        )

        startTimer()
    }

    fun startEmptyWorkout() {
        _uiState.value = ActiveWorkoutUiState(
            isActive = true,
            workoutTitle = "Quick Workout",
            elapsedTimeSeconds = 0,
            exercises = emptyList()
        )

        startTimer()
    }

    private fun startTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (_uiState.value.isActive) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    elapsedTimeSeconds = _uiState.value.elapsedTimeSeconds + 1
                )
            }
        }
    }

    fun toggleSetCompleted(exerciseName: String, setIndex: Int) {
        val state = _uiState.value
        val updatedExercises = state.exercises.map { ex ->
            if (ex.exerciseName == exerciseName) {
                val updatedSets = ex.sets.mapIndexed { idx, set ->
                    if (idx == setIndex) {
                        val newlyCompleted = !set.isCompleted
                        if (newlyCompleted) {
                            startRestTimer(60)
                        }
                        set.copy(isCompleted = newlyCompleted)
                    } else set
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.value = state.copy(exercises = updatedExercises)
    }

    fun updateSetWeight(exerciseName: String, setIndex: Int, newWeight: String) {
        val state = _uiState.value
        val updatedExercises = state.exercises.map { ex ->
            if (ex.exerciseName == exerciseName) {
                val updatedSets = ex.sets.mapIndexed { idx, set ->
                    if (idx == setIndex) set.copy(weightText = newWeight) else set
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.value = state.copy(exercises = updatedExercises)
    }

    fun updateSetReps(exerciseName: String, setIndex: Int, newReps: String) {
        val state = _uiState.value
        val updatedExercises = state.exercises.map { ex ->
            if (ex.exerciseName == exerciseName) {
                val updatedSets = ex.sets.mapIndexed { idx, set ->
                    if (idx == setIndex) set.copy(repsText = newReps) else set
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.value = state.copy(exercises = updatedExercises)
    }

    fun addSet(exerciseName: String) {
        val state = _uiState.value
        val updatedExercises = state.exercises.map { ex ->
            if (ex.exerciseName == exerciseName) {
                val lastSet = ex.sets.lastOrNull()
                val nextSetNum = (lastSet?.setNumber ?: 0) + 1
                val newSet = ActiveSetState(
                    setNumber = nextSetNum,
                    weightText = lastSet?.weightText ?: "0.0",
                    repsText = lastSet?.repsText ?: "10",
                    isCompleted = false
                )
                ex.copy(sets = ex.sets + newSet)
            } else ex
        }
        _uiState.value = state.copy(exercises = updatedExercises)
    }

    fun removeSet(exerciseName: String, setIndex: Int) {
        val state = _uiState.value
        val updatedExercises = state.exercises.map { ex ->
            if (ex.exerciseName == exerciseName && ex.sets.size > 1) {
                val updatedSets = ex.sets.filterIndexed { idx, _ -> idx != setIndex }
                    .mapIndexed { idx, set -> set.copy(setNumber = idx + 1) }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.value = state.copy(exercises = updatedExercises)
    }

    fun addExerciseToWorkout(exercise: Exercise) {
        val state = _uiState.value
        if (state.exercises.any { it.exerciseName == exercise.name }) return

        val newExerciseState = ActiveExerciseState(
            exerciseName = exercise.name,
            sets = listOf(
                ActiveSetState(setNumber = 1, weightText = "0.0", repsText = "10", isCompleted = false)
            )
        )
        _uiState.value = state.copy(exercises = state.exercises + newExerciseState)
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRestTimerActive = true,
            restSecondsRemaining = seconds
        )

        restTimerJob = viewModelScope.launch {
            while (_uiState.value.restSecondsRemaining > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    restSecondsRemaining = _uiState.value.restSecondsRemaining - 1
                )
            }
            _uiState.value = _uiState.value.copy(isRestTimerActive = false)
            triggerRestFinishedVibration()
        }
    }

    private fun triggerRestFinishedVibration() {
        val context = getApplication<Application>().applicationContext
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(500)
        }
    }

    fun adjustRestTimer(secondsDelta: Int) {
        val current = _uiState.value.restSecondsRemaining
        val newTime = (current + secondsDelta).coerceAtLeast(0)
        if (newTime == 0) {
            cancelRestTimer()
        } else {
            _uiState.value = _uiState.value.copy(restSecondsRemaining = newTime)
        }
    }

    fun cancelRestTimer() {
        restTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRestTimerActive = false, restSecondsRemaining = 0)
    }

    fun finishWorkout(notes: String) {
        val state = _uiState.value
        if (!state.isActive) return

        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        viewModelScope.launch {
            val session = WorkoutSession(
                planName = state.workoutTitle,
                timestamp = System.currentTimeMillis(),
                durationSeconds = state.elapsedTimeSeconds,
                notes = notes
            )

            val loggedSets = mutableListOf<LoggedSet>()
            state.exercises.forEach { exState ->
                exState.sets.forEach { setInput ->
                    if (setInput.isCompleted) {
                        loggedSets.add(
                            LoggedSet(
                                exerciseName = exState.exerciseName,
                                setNumber = setInput.setNumber,
                                weightKg = setInput.weightText.toDoubleOrNull() ?: 0.0,
                                reps = setInput.repsText.toIntOrNull() ?: 0,
                                isCompleted = true
                            )
                        )
                    }
                }
            }

            repository.saveCompletedWorkout(session, loggedSets)
            _uiState.value = ActiveWorkoutUiState(isActive = false, isFinished = true)
        }
    }

    fun cancelWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _uiState.value = ActiveWorkoutUiState(isActive = false)
    }
}

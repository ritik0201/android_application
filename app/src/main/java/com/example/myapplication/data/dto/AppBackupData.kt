package com.example.myapplication.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val name: String,
    val age: Int,
    val gender: String,
    val heightCm: Double,
    val weightKg: Double,
    val fitnessGoal: String
)

@Serializable
data class ExerciseDto(
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val instructions: String,
    val isCustom: Boolean
)

@Serializable
data class WorkoutPlanDto(
    val title: String,
    val description: String,
    val assignedDays: String
)

@Serializable
data class PlanExerciseDto(
    val exerciseName: String,
    val targetSets: Int,
    val targetReps: Int
)

@Serializable
data class PlanWithExercisesDto(
    val plan: WorkoutPlanDto,
    val exercises: List<PlanExerciseDto>
)

@Serializable
data class LoggedSetDto(
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val isCompleted: Boolean
)

@Serializable
data class WorkoutSessionDto(
    val planName: String,
    val timestamp: Long,
    val durationSeconds: Long,
    val notes: String,
    val sets: List<LoggedSetDto>
)

@Serializable
data class BodyMeasurementDto(
    val timestamp: Long,
    val weightKg: Double,
    val chestCm: Double,
    val waistCm: Double,
    val armsCm: Double,
    val thighsCm: Double
)

@Serializable
data class MealLogDto(
    val timestamp: Long,
    val mealName: String,
    val mealType: String,
    val calories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double
)

@Serializable
data class AppBackupData(
    val version: Int = 1,
    val exportTimestamp: Long = System.currentTimeMillis(),
    val profile: UserProfileDto? = null,
    val exercises: List<ExerciseDto> = emptyList(),
    val workoutPlans: List<PlanWithExercisesDto> = emptyList(),
    val workoutSessions: List<WorkoutSessionDto> = emptyList(),
    val bodyMeasurements: List<BodyMeasurementDto> = emptyList(),
    val mealLogs: List<MealLogDto> = emptyList()
)

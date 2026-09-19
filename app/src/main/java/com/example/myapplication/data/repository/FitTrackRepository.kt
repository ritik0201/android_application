package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.*
import com.example.myapplication.data.database.ExerciseSeedData
import com.example.myapplication.data.dto.*
import com.example.myapplication.data.entity.*
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Repository layer connecting Room DAOs and DataStore with ViewModels.
 */
class FitTrackRepository(
    private val userProfileDao: UserProfileDao,
    private val exerciseDao: ExerciseDao,
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val bodyMeasurementDao: BodyMeasurementDao,
    private val nutritionDao: NutritionDao,
    private val achievementDao: AchievementDao,
    private val progressPhotoDao: ProgressPhotoDao,
    private val exerciseNoteDao: ExerciseNoteDao,
    private val userPreferences: UserPreferences
) {
    val isOnboardingCompleted: Flow<Boolean> = userPreferences.isOnboardingCompleted
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val exercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()
    val workoutPlans: Flow<List<WorkoutPlanWithExercises>> = workoutPlanDao.getAllPlansWithExercises()
    val workoutSessions: Flow<List<WorkoutSessionWithSets>> = workoutSessionDao.getAllSessionsWithSets()
    val bodyMeasurements: Flow<List<BodyMeasurement>> = bodyMeasurementDao.getAllMeasurements()
    val mealLogs: Flow<List<MealLog>> = nutritionDao.getAllMealLogs()
    val achievements: Flow<List<Achievement>> = achievementDao.getAllAchievements()
    val progressPhotos: Flow<List<ProgressPhoto>> = progressPhotoDao.getAllPhotos()

    fun getWaterLogForDate(dateString: String): Flow<WaterLog?> = nutritionDao.getWaterLogByDate(dateString)
    fun getExerciseNote(exerciseName: String): Flow<ExerciseNote?> = exerciseNoteDao.getNoteForExercise(exerciseName)

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile)
        userPreferences.setOnboardingCompleted(true)
    }

    suspend fun seedExercisesIfEmpty() {
        if (exerciseDao.getExerciseCount() == 0) {
            exerciseDao.insertExercises(ExerciseSeedData.initialExercises)
        }
        seedAchievementsIfEmpty()
    }

    private suspend fun seedAchievementsIfEmpty() {
        val initialAchievements = listOf(
            Achievement("first_workout", "First Sweat", "Completed your first workout session!"),
            Achievement("streak_7", "Consistency Master", "Maintained a 7-day workout streak!"),
            Achievement("sessions_10", "10 Workouts Club", "Completed 10 workout sessions!"),
            Achievement("first_pr", "PR Crusher", "Set your first personal record in an exercise!")
        )
        achievementDao.insertAchievements(initialAchievements)
    }

    suspend fun unlockAchievement(id: String) {
        achievementDao.unlockAchievement(id)
    }

    suspend fun insertExercise(exercise: Exercise) { exerciseDao.insertExercise(exercise) }
    suspend fun updateExercise(exercise: Exercise) { exerciseDao.updateExercise(exercise) }
    suspend fun deleteExercise(exercise: Exercise) { exerciseDao.deleteExercise(exercise) }

    suspend fun saveWorkoutPlan(plan: WorkoutPlan, exercises: List<PlanExercise>) {
        if (plan.id == 0) {
            workoutPlanDao.insertPlanWithExercises(plan, exercises)
        } else {
            workoutPlanDao.updatePlanWithExercises(plan, exercises)
        }
    }

    suspend fun deleteWorkoutPlan(plan: WorkoutPlan) { workoutPlanDao.deleteWorkoutPlan(plan) }

    suspend fun saveCompletedWorkout(session: WorkoutSession, sets: List<LoggedSet>) {
        workoutSessionDao.saveCompletedWorkout(session, sets)
        unlockAchievement("first_workout")
    }

    suspend fun deleteWorkoutSession(session: WorkoutSession) { workoutSessionDao.deleteWorkoutSession(session) }

    suspend fun insertMeasurement(measurement: BodyMeasurement) { bodyMeasurementDao.insertMeasurement(measurement) }
    suspend fun deleteMeasurement(measurement: BodyMeasurement) { bodyMeasurementDao.deleteMeasurement(measurement) }

    suspend fun insertMealLog(mealLog: MealLog) { nutritionDao.insertMealLog(mealLog) }
    suspend fun deleteMealLog(mealLog: MealLog) { nutritionDao.deleteMealLog(mealLog) }
    suspend fun insertOrUpdateWaterLog(waterLog: WaterLog) { nutritionDao.insertOrUpdateWaterLog(waterLog) }

    suspend fun saveProgressPhoto(photo: ProgressPhoto) { progressPhotoDao.insertPhoto(photo) }
    suspend fun deleteProgressPhoto(photo: ProgressPhoto) { progressPhotoDao.deletePhoto(photo) }

    suspend fun saveExerciseNote(exerciseName: String, text: String) {
        exerciseNoteDao.insertOrUpdateNote(ExerciseNote(exerciseName, text))
    }

    suspend fun exportJsonBackup(): String {
        val p = userProfile.firstOrNull()
        val pDto = p?.let { UserProfileDto(it.name, it.age, it.gender, it.heightCm, it.weightKg, it.fitnessGoal) }

        val exList = exercises.firstOrNull() ?: emptyList()
        val exDtos = exList.map { ExerciseDto(it.name, it.muscleGroup, it.equipment, it.instructions, it.isCustom) }

        val plans = workoutPlans.firstOrNull() ?: emptyList()
        val planDtos = plans.map { planWithEx ->
            PlanWithExercisesDto(
                plan = WorkoutPlanDto(planWithEx.plan.title, planWithEx.plan.description, planWithEx.plan.assignedDays),
                exercises = planWithEx.exercises.map { PlanExerciseDto(it.exerciseName, it.targetSets, it.targetReps) }
            )
        }

        val backup = AppBackupData(
            profile = pDto,
            exercises = exDtos,
            workoutPlans = planDtos
        )
        return Json.encodeToString(backup)
    }

    suspend fun restoreJsonBackup(jsonString: String): Boolean {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val backup = json.decodeFromString<AppBackupData>(jsonString)

            backup.profile?.let { p ->
                userProfileDao.insertOrUpdateProfile(
                    UserProfile(name = p.name, age = p.age, gender = p.gender, heightCm = p.heightCm, weightKg = p.weightKg, fitnessGoal = p.fitnessGoal)
                )
                userPreferences.setOnboardingCompleted(true)
            }

            backup.exercises.forEach { ex ->
                exerciseDao.insertExercise(
                    Exercise(name = ex.name, muscleGroup = ex.muscleGroup, equipment = ex.equipment, instructions = ex.instructions, isCustom = ex.isCustom)
                )
            }

            backup.workoutPlans.forEach { pWithEx ->
                val plan = WorkoutPlan(title = pWithEx.plan.title, description = pWithEx.plan.description, assignedDays = pWithEx.plan.assignedDays)
                val planExs = pWithEx.exercises.map {
                    PlanExercise(exerciseId = 0, exerciseName = it.exerciseName, targetSets = it.targetSets, targetReps = it.targetReps)
                }
                workoutPlanDao.insertPlanWithExercises(plan, planExs)
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearAllData() {
        userPreferences.clearAll()
    }
}

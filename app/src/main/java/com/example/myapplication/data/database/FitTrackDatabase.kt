package com.example.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.*
import com.example.myapplication.data.entity.*

/**
 * Main Room database singleton for FitTrack app.
 */
@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        WorkoutPlan::class,
        PlanExercise::class,
        WorkoutSession::class,
        LoggedSet::class,
        BodyMeasurement::class,
        MealLog::class,
        WaterLog::class,
        Achievement::class,
        ProgressPhoto::class,
        ExerciseNote::class
    ],
    version = 5,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun progressPhotoDao(): ProgressPhotoDao
    abstract fun exerciseNoteDao(): ExerciseNoteDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

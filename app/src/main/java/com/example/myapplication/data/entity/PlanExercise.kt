package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity mapping exercises to a WorkoutPlan with target sets & reps.
 */
@Entity(
    tableName = "plan_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId")]
)
data class PlanExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int = 0,
    val exerciseId: Int,
    val exerciseName: String,
    val targetSets: Int = 3,
    val targetReps: Int = 10
)

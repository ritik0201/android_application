package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a Workout Plan / Routine.
 */
@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val assignedDays: String = "" // e.g. "Monday, Wednesday, Friday"
)

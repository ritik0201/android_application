package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a completed or logged workout session.
 */
@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0,
    val notes: String = ""
)

package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing per-exercise notes and equipment setup references.
 */
@Entity(tableName = "exercise_notes")
data class ExerciseNote(
    @PrimaryKey val exerciseName: String,
    val noteText: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

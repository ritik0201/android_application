package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing body weight and measurement logs.
 */
@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val weightKg: Double = 0.0,
    val chestCm: Double = 0.0,
    val waistCm: Double = 0.0,
    val armsCm: Double = 0.0,
    val thighsCm: Double = 0.0
)

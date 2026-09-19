package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing the user's fitness profile.
 * Stored with a fixed ID of 1 since there is only one user profile.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val gender: String,
    val heightCm: Double,
    val weightKg: Double,
    val fitnessGoal: String
)

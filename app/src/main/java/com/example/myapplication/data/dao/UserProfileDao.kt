package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserProfile database operations.
 */
@Dao
interface UserProfileDao {

    // Retrieve user profile as a Flow to observe changes automatically
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    // Insert or replace user profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}

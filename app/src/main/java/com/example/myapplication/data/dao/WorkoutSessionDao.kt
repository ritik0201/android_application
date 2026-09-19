package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myapplication.data.entity.LoggedSet
import com.example.myapplication.data.entity.WorkoutSession
import com.example.myapplication.data.entity.WorkoutSessionWithSets
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for WorkoutSession and LoggedSet database operations.
 */
@Dao
interface WorkoutSessionDao {

    @Transaction
    @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC")
    fun getAllSessionsWithSets(): Flow<List<WorkoutSessionWithSets>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionWithSetsById(sessionId: Int): Flow<WorkoutSessionWithSets?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession): Long

    @Update
    suspend fun updateWorkoutSession(session: WorkoutSession)

    @Delete
    suspend fun deleteWorkoutSession(session: WorkoutSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedSets(sets: List<LoggedSet>)

    @Query("DELETE FROM logged_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsForSession(sessionId: Int)

    @Transaction
    suspend fun saveCompletedWorkout(session: WorkoutSession, sets: List<LoggedSet>) {
        val sessionId = insertWorkoutSession(session).toInt()
        val updatedSets = sets.map { it.copy(sessionId = sessionId) }
        insertLoggedSets(updatedSets)
    }

    @Transaction
    suspend fun updateWorkoutWithSets(session: WorkoutSession, sets: List<LoggedSet>) {
        updateWorkoutSession(session)
        deleteSetsForSession(session.id)
        val updatedSets = sets.map { it.copy(sessionId = session.id) }
        insertLoggedSets(updatedSets)
    }
}

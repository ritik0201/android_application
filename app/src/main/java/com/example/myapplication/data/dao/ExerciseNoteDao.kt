package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.entity.ExerciseNote
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseNoteDao {
    @Query("SELECT * FROM exercise_notes WHERE exerciseName = :exerciseName LIMIT 1")
    fun getNoteForExercise(exerciseName: String): Flow<ExerciseNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: ExerciseNote)
}

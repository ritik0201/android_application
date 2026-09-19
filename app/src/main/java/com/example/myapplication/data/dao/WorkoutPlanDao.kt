package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myapplication.data.entity.PlanExercise
import com.example.myapplication.data.entity.WorkoutPlan
import com.example.myapplication.data.entity.WorkoutPlanWithExercises
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for WorkoutPlan and PlanExercise database operations.
 */
@Dao
interface WorkoutPlanDao {

    @Transaction
    @Query("SELECT * FROM workout_plans ORDER BY id DESC")
    fun getAllPlansWithExercises(): Flow<List<WorkoutPlanWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    fun getPlanWithExercisesById(planId: Int): Flow<WorkoutPlanWithExercises?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlan)

    @Delete
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExercises(exercises: List<PlanExercise>)

    @Query("DELETE FROM plan_exercises WHERE planId = :planId")
    suspend fun deletePlanExercisesForPlan(planId: Int)

    @Transaction
    suspend fun insertPlanWithExercises(plan: WorkoutPlan, exercises: List<PlanExercise>) {
        val planId = insertWorkoutPlan(plan).toInt()
        val updatedExercises = exercises.map { it.copy(planId = planId) }
        insertPlanExercises(updatedExercises)
    }

    @Transaction
    suspend fun updatePlanWithExercises(plan: WorkoutPlan, exercises: List<PlanExercise>) {
        updateWorkoutPlan(plan)
        deletePlanExercisesForPlan(plan.id)
        val updatedExercises = exercises.map { it.copy(planId = plan.id) }
        insertPlanExercises(updatedExercises)
    }
}

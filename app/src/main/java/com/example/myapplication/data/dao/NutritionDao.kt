package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.entity.MealLog
import com.example.myapplication.data.entity.WaterLog
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for MealLog and WaterLog database operations.
 */
@Dao
interface NutritionDao {

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLog)

    @Update
    suspend fun updateMealLog(mealLog: MealLog)

    @Delete
    suspend fun deleteMealLog(mealLog: MealLog)

    @Query("SELECT * FROM water_logs WHERE dateString = :dateString LIMIT 1")
    fun getWaterLogByDate(dateString: String): Flow<WaterLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWaterLog(waterLog: WaterLog)
}

package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.entity.BodyMeasurement
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for BodyMeasurement database operations.
 */
@Dao
interface BodyMeasurementDao {

    @Query("SELECT * FROM body_measurements ORDER BY timestamp ASC")
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurement)

    @Update
    suspend fun updateMeasurement(measurement: BodyMeasurement)

    @Delete
    suspend fun deleteMeasurement(measurement: BodyMeasurement)
}

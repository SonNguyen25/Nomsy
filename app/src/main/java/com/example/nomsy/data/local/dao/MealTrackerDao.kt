package com.example.nomsy.data.local.dao;

import androidx.room.*
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.entities.MealEntity
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.local.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MealTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    // get meals for a date
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY timestamp")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>


    @Query("SELECT * FROM meals WHERE date = :date ORDER BY mealType, timestamp")
    fun getMealsGroupedByType(date: String): Flow<List<MealEntity>>

    // delete by meal ID
    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    // Insert or update daily summary

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailySummaryEntity)

    /**
     * Get daily summary for a specific date
     */
    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    fun getDailySummary(date: String): Flow<DailySummaryEntity?>
}

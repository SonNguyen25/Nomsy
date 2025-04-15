package com.example.nomsy.data.local.dao;

import androidx.room.*
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.entities.MealEntity
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.local.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MealTrackerDao {
    // Meal operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY mealType, time ASC")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE date = :date AND mealType = :mealType ORDER BY time ASC")
    fun getMealsByDateAndType(date: String, mealType: String): Flow<List<MealEntity>>

    // not using
    @Query("DELETE FROM meals WHERE date = :date")
    suspend fun deleteMealsByDate(date: String)

    // all of daily summary...
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailySummaryEntity)

    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    fun getDailySummaryByDate(date: String): Flow<DailySummaryEntity?>

    @Query("UPDATE daily_summaries SET waterLiters = :waterLiters WHERE date = :date")
    suspend fun updateWaterIntake(date: String, waterLiters: Double)

    // Transaction to delete old data and insert new data -  may or may not be useful.
    @Transaction
    suspend fun updateDailySummaryWithMeals(summary: DailySummaryEntity, meals: List<MealEntity>) {
        deleteMealsByDate(summary.date)
        insertDailySummary(summary)
        insertMeals(meals)
    }

    // DELETE FOOD by name and date
    @Query("DELETE FROM meals WHERE date = :date AND food_name = :food_name")
    suspend fun deleteMealByDateAndName(date: String, food_name: String): Int

}

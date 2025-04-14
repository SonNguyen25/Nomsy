package com.example.nomsy.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nomsy.data.local.models.Food

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<Food>)

    @Query("SELECT * FROM foods WHERE date = :date")
    suspend fun getFoodsByDate(date: String): List<Food>

    @Query("SELECT * FROM foods WHERE date = :date AND meal_Type = :mealType")
    suspend fun getFoodsByDateAndType(date: String, mealType: String): List<Food>

    @Query("DELETE FROM foods WHERE date = :date")
    suspend fun deleteByDate(date: String)
    
    @Query("DELETE FROM foods")
    suspend fun clearAll()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food)

    @Query("SELECT * FROM foods ORDER BY date DESC")
    suspend fun getAllFoods(): List<Food>
}
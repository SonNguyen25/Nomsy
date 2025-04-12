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

    @Query("SELECT * FROM foods WHERE date = :date AND mealType = :mealType")
    suspend fun getFoodsByDateAndType(date: String, mealType: String): List<Food>

    @Query("DELETE FROM foods WHERE date = :date")
    suspend fun deleteByDate(date: String)
    
    @Query("DELETE FROM foods")
    suspend fun clearAll()
}
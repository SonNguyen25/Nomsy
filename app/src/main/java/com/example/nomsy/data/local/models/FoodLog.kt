package com.example.nomsy.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey val id: String,
    val username: String,
    val foodId: String,  // Reference to the Food id
    val name: String,    // Denormalized for convenience
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val servingSize: Float,
    val servingUnit: String,
    val servings: Float,
    val mealType: String, // breakfast, lunch, dinner, snack
    val imageUrl: String? = null,
    val date: Long, // timestamp for the day
    val time: Long = System.currentTimeMillis() // when this entry was created
) {
    // Factory method to create FoodLog from Food
    companion object {
        fun fromFood(
            id: String,
            food: Food,
            username: String,
            servings: Float = 1f,
            mealType: String,
            servingSize: Float = 100f,
            servingUnit: String = "g",
            date: Long = System.currentTimeMillis(),
            imageUrl: String? = null
        ): FoodLog {
            // Calculate nutrition based on serving size
            val servingMultiplier = servings

            return FoodLog(
                id = id,
                username = username,
                foodId = food.id,
                name = food.food_name,
                calories = (food.calories * servingMultiplier).toInt(),
                protein = (food.protein * servingMultiplier).toFloat(),
                carbs = (food.carbs * servingMultiplier).toFloat(),
                fat = (food.fat * servingMultiplier).toFloat(),
                servingSize = servingSize,
                servingUnit = servingUnit,
                servings = servings,
                mealType = mealType,
                imageUrl = imageUrl,
                date = date
            )
        }
    }
}
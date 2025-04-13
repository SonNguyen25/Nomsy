package com.example.nomsy.data.remote


import retrofit2.Response
import retrofit2.http.*

interface MealTrackerApiService {
    @POST("meals")
    suspend fun addMeal(@Body mealRequest: AddMealRequest): Response<AddMealResponse>

    @GET("daily-summary")
    suspend fun getDailySummary(@Query("date") date: String): Response<DailySummaryResponse>
}

// Request model for adding a meal
data class AddMealRequest(
    val date: String,          // "YYYY-MM-DD"
    val meal_type: String,     // "breakfast", "lunch", "dinner", "snack"
    val food_name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

// Response model for adding a meal
data class AddMealResponse(
    val message: String,
    val meal_id: String
)

// Response models for daily summary
data class DailySummaryResponse(
    val date: String,
    val totals: NutritionTotals,
    val meals: Map<String, List<MealItem>>
)

data class NutritionTotals(
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val water: Double
)

data class MealItem(
    val food_name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)
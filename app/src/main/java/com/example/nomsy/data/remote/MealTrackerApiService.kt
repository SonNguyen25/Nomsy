package com.example.nomsy.data.remote


import androidx.room.PrimaryKey
import com.example.nomsy.data.local.models.Food
import retrofit2.Response
import retrofit2.http.*

interface MealTrackerApiService {
    @POST("meals")
    suspend fun addMeal(@Body mealRequest: AddMealRequest): Response<AddMealResponse>

    @GET("daily-summary")
    suspend fun getDailySummary(@Query("date") date: String): Response<DailySummaryResponse>

    @DELETE("/meal")
    suspend fun deleteMeal(
        @Query("date") date: String,
        @Query("food_name") foodName: String
    ): Response<DeleteMealResponse>

    @GET("/foods")
    suspend fun getAllFoods(): Response<FoodResponse>

}


data class FoodResponse(
    val foods: List<Food>
)

// model for delete meal response
data class DeleteMealResponse(
    val message: String,
    val success: Boolean
)

// Request model for adding a meal
data class AddMealRequest(
    val date: String,
    val meal_type: String,
    val food_name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

// Response model for adding a meal
data class AddMealResponse(
    val message: String,
    val mealId: String
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
) {

}
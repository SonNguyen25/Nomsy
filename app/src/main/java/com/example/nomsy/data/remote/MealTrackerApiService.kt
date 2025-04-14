package com.example.nomsy.data.remote


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
}

// model for delete meal response
data class DeleteMealResponse(
    val message: String,
    val success: Boolean
)

// Request model for adding a meal
data class AddMealRequest(
    val date: String,          // "YYYY-MM-DD"
    val mealType: String,     // "breakfast", "lunch", "dinner"
    val foodName: String,
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
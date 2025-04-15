package com.example.nomsy.data.remote


import com.example.nomsy.data.local.entities.Food
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface MealTrackerApiService {
    @POST("meals")
    suspend fun addMeal(@Body mealRequest: AddMealRequest): Response<AddMealResponse>

    @PATCH("water")
    suspend fun adjustWater(@Body data: AdjustWaterRequest): WaterResponse

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

// REQUEST AND RESPONSE MODELS - sorry it's not organized

data class AdjustWaterRequest(
    val date: String,
    val delta: Double
)

data class WaterResponse(
    val date: String,
    val water: Double
)

data class FoodResponse(
    val foods: List<Food>
)

data class DeleteMealResponse(
    val message: String,
    val success: Boolean
)

data class AddMealRequest(
    val date: String,
    val meal_type: String,
    val food_name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

data class AddMealResponse(
    val message: String,
    val mealId: String
)

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
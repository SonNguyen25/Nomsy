package com.example.nomsy.data.repository

import android.content.Context
import android.util.Log
import com.example.nomsy.data.local.dao.MealTrackerDao
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.entities.MealEntity
import com.example.nomsy.data.local.entities.toMealItem
import com.example.nomsy.data.remote.*
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MealTrackerRepository(
    private val mealApiService: MealTrackerApiService = MealTrackerRetrofitClient.mealTrackerApi,
    private val mealDao: MealTrackerDao,
) {
    // Get nutrition totals for a specific date
    suspend fun getDailyNutritionTotals(date: String): Result<Flow<DailySummaryEntity?>> {
        return try {
            val response = mealApiService.getDailySummary(date)
            if (response.isSuccessful) {
                val summaryResponse = response.body()
                summaryResponse?.let {
                    //  save to database
                    val dailySummaryEntity = DailySummaryEntity(
                        date = it.date,
                        totalCalories = it.totals.calories,
                        totalCarbs = it.totals.carbs,
                        totalProtein = it.totals.protein,
                        totalFat = it.totals.fat,
                        waterLiters = it.totals.water
                    )
                    mealDao.insertDailySummary(dailySummaryEntity)
                }

                // Return data from local database as Flow
                Result.Success(mealDao.getDailySummaryByDate(date))
            } else {
                // If API call fails, try to fetch from local database
                Result.Success(mealDao.getDailySummaryByDate(date))
            }
        } catch (e: IOException) {
            // Network error - return local data if available
            Result.Success(mealDao.getDailySummaryByDate(date))
        } catch (e: Exception) {
            Result.Error(Exception(e.message))
        }
    }

    // Get meals for a specific date
    suspend fun getMealsByDate(date: String): Result<Map<String, List<MealItem>>> {
        return try {
            // Fetch from remote API
            val response = mealApiService.getDailySummary(date)

            if (response.isSuccessful) {
                Log.d("MealTrackerRepository", "Meals get by date loaded successfully: $response")
                val summaryResponse = response.body()

                //DEBUGGING
                if (summaryResponse != null) {
                    Log.d("MealTrackerRepository", "Response meals: ${summaryResponse.meals}")

                    // Print a sample meal if available
                    if (summaryResponse.meals.isNotEmpty()) {
                        val firstMealType = summaryResponse.meals.keys.first()
                        val firstMealList = summaryResponse.meals[firstMealType]
                        if (!firstMealList.isNullOrEmpty()) {
                            Log.d(
                                "MealTrackerRepository",
                                "Sample meal properties: ${firstMealList[0]}"
                            )
                        }
                    }

                    // Rest of your code...
                }
                if (summaryResponse != null) {

                    // Extract meals from response
                    val mealsMap = summaryResponse.meals.mapValues { (_, meals) ->
                        meals.map { meal ->
                            MealItem(
                                food_name = meal.food_name,
                                calories = meal.calories,
                                carbs = meal.carbs,
                                protein = meal.protein,
                                fat = meal.fat
                            )
                        }
                    }

                    // update DB for offline access
                    val mealEntities = mutableListOf<MealEntity>()
                    mealsMap.forEach { (mealType, meals) ->
                        meals.forEach { meal ->
                            mealEntities.add(
                                MealEntity(
                                    mealId = "",
                                    date = date,
                                    mealType = mealType,
                                    food_name = meal.food_name,
                                    calories = meal.calories,
                                    carbs = meal.carbs,
                                    protein = meal.protein,
                                    fat = meal.fat
                                )
                            )
                        }
                    }

                    // Save to database
                    mealDao.deleteMealsByDate(date)
                    mealDao.insertMeals(mealEntities)

                    Result.Success(mealsMap)
                } else {
                    Result.Error(Exception("No meal data available for date"))
                }
            } else {
                // API call failed, fetch from local database
                val localMeals = mealDao.getMealsByDate(date)
                    .flowOn(Dispatchers.IO)
                    .map { meals ->
                        // group meals here - make map by mealType
                        meals.groupBy { it.mealType }
                            .mapValues { (_, mealList) ->
                                mealList.map { it.toMealItem() }
                            }
                    }.first()
                Result.Success(localMeals)
            }
        } catch (e: IOException) {
            // Network error
            try {
                val localMeals = mealDao.getMealsByDate(date).map { meals ->
                    meals.groupBy { it.mealType }
                        .mapValues { (_, mealList) ->
                            mealList.map { it.toMealItem() }
                        }
                }.first()
                Result.Success(localMeals)
            } catch (e: Exception) {
                Result.Error(Exception("No meal data available for date"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("an unknown error has occurred"))
        }
    }


    suspend fun deleteMeal(date: String, foodName: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MealTrackerRepository", "Deleting meal: $foodName on date: $date")
                val response = mealApiService.deleteMeal(date, foodName)
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    Log.d("MealTrackerRepository", "API delete response: $deleteResponse")

                    if (deleteResponse?.success == true) {

                        mealDao.deleteMealByDateAndName(date, foodName)
                        Result.Success(true)
                    } else {
                        Result.Error(Exception(deleteResponse?.message ?: "Failed to delete meal"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.Error(Exception("deletion failed: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("MealTrackerRepository", "deletion failed with exception ", e)
                Result.Error(e)
            }
        }
    }

    suspend fun updateWaterIntakeDelta(date: String, delta: Double, newAmount: Double): Double {
        try {
            val request = AdjustWaterRequest(date, delta)
            val response = mealApiService.adjustWater(request)
            mealDao.updateWaterIntake(date, newAmount)
            return response.water
        } catch (e: Exception) {
            Log.e("Repository", "Error updating water", e)
            throw e
        }
    }

    suspend fun addMeal(
        date: String,
        mealType: String,
        foodName: String,
        calories: Int,
        carbs: Int,
        protein: Int,
        fat: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {

            val request = AddMealRequest(
                date = date,
                meal_type = mealType,
                food_name = foodName,
                calories = calories,
                carbs = carbs,
                protein = protein,
                fat = fat
            )


            val response = mealApiService.addMeal(request)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {

                    Result.Success(data.mealId)

                } else {
                    Result.Error(Exception("Empty response body"))
                }
            } else {
                Result.Error(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateWaterIntake(date: String, waterLiters: Double): Result<Unit> {
        return try {
            mealDao.updateWaterIntake(date, waterLiters)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}
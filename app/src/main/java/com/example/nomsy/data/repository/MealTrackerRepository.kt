package com.example.nomsy.data.repository

import android.content.Context
import com.example.nomsy.data.remote.*
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MealTrackerRepository(
    private val apiService: MealTrackerApiService,
    private val context: Context
) {
    // by lazy lets us create only when needed to save resources
//    private val database: MealTrackerDatabase by lazy {
//        MealTrackerDatabase.getDatabase(context)
//    }
//    private val mealTrackerDao: MealTrackerDao by lazy {
//        database.mealTrackerDao()
//    }


    fun getDailySummary(date: String): Flow<Result<DailySummaryResponse>> = flow {
        emit(Result.Loading)
        try {
            // Convert date to YYYY-MM-DD

            val response = apiService.getDailySummary(date)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(Result.Success(data))
                } else {
                    emit(Result.Error(Exception("Empty response body")))
                }
            } else {
                emit(Result.Error(Exception("Error ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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
            val response = apiService.addMeal(request)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Result.Success(data.meal_id)
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


    // converts YYYY-MM-DD string format into Long from API
    private fun parseApiDate(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
    }
}
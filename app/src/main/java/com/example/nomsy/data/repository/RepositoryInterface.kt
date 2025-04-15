package com.example.nomsy.data.repository

import androidx.lifecycle.LiveData
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.data.local.entities.User
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.data.remote.AddMealResponse
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.utils.Result
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun login(username: String, password: String): LiveData<Result<User>>
    fun register(user: User): LiveData<Result<User>>
    fun getProfile(userId: String): LiveData<Result<User>>
    fun getProfileByUsername(username: String): LiveData<Result<User>>
    fun updateProfile(
        username: String, request: UpdateProfileRequest
    ): LiveData<Result<User>>
}

interface IFoodRepository {
    fun addMeal(mealRequest: AddMealRequest): LiveData<Result<AddMealResponse>>
}

interface IRecipeRepository {
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun searchRecipes(query: String): List<Recipe>
}

interface IMealTrackerRepository {

    suspend fun getDailyNutritionTotals(date: String): Result<Flow<DailySummaryEntity?>>

    suspend fun getMealsByDate(date: String): Result<Map<String, List<MealItem>>>

    suspend fun deleteMeal(date: String, foodName: String): Result<Boolean>

    suspend fun updateWaterIntakeDelta(date: String, delta: Double, newAmount: Double): Double

    suspend fun addMeal(
        date: String,
        mealType: String,
        foodName: String,
        calories: Int,
        carbs: Int,
        protein: Int,
        fat: Int
    ): Result<String>

}
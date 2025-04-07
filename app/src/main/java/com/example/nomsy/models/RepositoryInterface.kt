package com.example.nomsy.models
import androidx.lifecycle.LiveData

interface IUserRepository {
    fun login(username: String, password: String): LiveData<Result<User>>
    fun register(user: User): LiveData<Result<User>>
    fun getProfile(userId: String): LiveData<Result<User>>
}

interface IFoodRepository {
    fun addFood(food: Food): LiveData<Result<Food>>
    fun getFoods(userId: String): LiveData<Result<List<Food>>>
}

interface IRecipeRepository {
    fun fetchRecipes(): LiveData<Result<List<Recipe>>>
}
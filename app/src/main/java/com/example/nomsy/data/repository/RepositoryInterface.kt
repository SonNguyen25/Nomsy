package com.example.nomsy.data.repository

import androidx.lifecycle.LiveData
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.data.remote.AddMealResponse
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.utils.Result
import com.google.android.gms.common.api.Response

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
}
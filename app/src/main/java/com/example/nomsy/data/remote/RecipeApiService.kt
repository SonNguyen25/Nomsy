package com.example.nomsy.data.remote

import com.example.nomsy.responses.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeAPIService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    @GET("search.php")
    suspend fun getAllRecipes(@Query("s") search: String = ""): MealResponse
}

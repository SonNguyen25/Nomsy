package com.example.nomsy.data.repository

import com.example.nomsy.data.local.dao.RecipeDAO
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.network.RecipeAPIService


class RecipeRepository(
    private val api: RecipeAPIService,
    private val dao: RecipeDAO
) {
    suspend fun searchRecipes(query: String): List<Recipe> {
        val result = api.searchMeals(query).meals.orEmpty()
        dao.insertRecipes(result)
        return result
    }

    suspend fun getAllLocalRecipes(): List<Recipe> = dao.getAllRecipes()
}

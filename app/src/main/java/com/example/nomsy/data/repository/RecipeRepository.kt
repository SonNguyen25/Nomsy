package com.example.nomsy.data.repository

import com.example.nomsy.data.local.dao.RecipeDAO
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.local.models.toRecipe
import com.example.nomsy.data.remote.RecipeAPIService

class RecipeRepository(
    private val api: RecipeAPIService,
    private val dao: RecipeDAO
) {
    suspend fun searchRecipes(query: String): List<Recipe> {
        val result = api.searchMeals(query).meals?.map { it.toRecipe() }.orEmpty()
        dao.insertRecipes(result)
        return result
    }

    suspend fun getAllRecipes(): List<Recipe> {
        val result = api.getAllRecipes().meals?.map { it.toRecipe() }.orEmpty()
        dao.insertRecipes(result)
        return result
    }
}

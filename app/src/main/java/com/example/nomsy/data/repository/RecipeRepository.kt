package com.example.nomsy.data.repository

import com.example.nomsy.data.local.dao.RecipeDAO
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.local.models.toRecipe
import com.example.nomsy.data.remote.RecipeAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class RecipeRepository(
    private val api: RecipeAPIService,
    private val dao: RecipeDAO
) : IRecipeRepository {
    suspend fun searchRecipes(query: String): List<Recipe> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = api.searchMeals(query).meals?.map { it.toRecipe() }.orEmpty()
            dao.insertRecipes(result)
            result
        } catch (e: IOException) {
            e.printStackTrace()
            dao.getAllRecipes() // fallback to cache if no network
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAllRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = api.getAllRecipes().meals?.map { it.toRecipe() }.orEmpty()
            dao.insertRecipes(result)
            result
        } catch (e: IOException) {
            e.printStackTrace()
            dao.getAllRecipes() // fallback to cached data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
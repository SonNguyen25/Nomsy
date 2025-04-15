package com.example.nomsy.viewModels

import com.example.nomsy.data.local.models.Recipe
import kotlinx.coroutines.flow.StateFlow

interface IRecipeViewModel {
    val recipes: StateFlow<List<Recipe>>
    val recipesByCategory: StateFlow<Map<String, List<Recipe>>>

    fun search(query: String)
    fun loadAllRecipes()
}
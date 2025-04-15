package com.example.nomsy.ui.screens.recipe

import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.viewModels.IRecipeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeRecipeViewModel : IRecipeViewModel {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override val recipes: StateFlow<List<Recipe>> get() = _recipes

    private val _recipesByCategory = MutableStateFlow<Map<String, List<Recipe>>>(emptyMap())
    override val recipesByCategory: StateFlow<Map<String, List<Recipe>>> get() = _recipesByCategory

    private val sampleRecipes =
        listOf(
        Recipe("1", "Chicken Curry", "Indian", "", "", "", "", "", emptyList()),
        Recipe("2", "Tacos", "Mexican", "", "", "", "", "", emptyList() ),
        Recipe("3", "Pasta", "Italian", "", "", "", "", "", emptyList())
    )

    override fun loadAllRecipes() {
        _recipes.value = sampleRecipes
        _recipesByCategory.value = sampleRecipes.groupBy { it.strCategory ?: "Uncategorized" }
    }

    override fun search(query: String) {
        val filtered = sampleRecipes.filter { it.strMeal.contains(query, ignoreCase = true) }
        _recipes.value = filtered
        _recipesByCategory.value = filtered.groupBy { it.strCategory ?: "Uncategorized" }
    }
}

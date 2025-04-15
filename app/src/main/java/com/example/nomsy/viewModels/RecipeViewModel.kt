package com.example.nomsy.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel(), IRecipeViewModel {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override val recipes: StateFlow<List<Recipe>> = _recipes
    private val _recipesByCategory = MutableStateFlow<Map<String, List<Recipe>>>(emptyMap())
    override val recipesByCategory: StateFlow<Map<String, List<Recipe>>> = _recipesByCategory

    override fun search(query: String) {
        viewModelScope.launch {
            val result = repository.searchRecipes(query)
            _recipes.value = result
            _recipesByCategory.value = result.groupBy { it.strCategory ?: "Uncategorized" }
        }
    }


    override fun loadAllRecipes() {
        viewModelScope.launch {
            val allRecipes = repository.getAllRecipes()
            _recipes.value = allRecipes
            _recipesByCategory.value = allRecipes.groupBy { it.strCategory ?: "Uncategorized" }
        }
    }
}

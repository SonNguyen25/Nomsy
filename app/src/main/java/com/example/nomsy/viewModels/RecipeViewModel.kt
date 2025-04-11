package com.example.nomsy.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.data.repository.IRecipeRepository

class RecipeViewModel(private val recipeRepository: IRecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<Result<List<Recipe>>>()
    val recipes: LiveData<Result<List<Recipe>>> = _recipes

    fun fetchRecipes() {
        _recipes.value = Result.loading()
        recipeRepository.fetchRecipes().observeForever {
//            _recipes.value = it
        }
    }
}
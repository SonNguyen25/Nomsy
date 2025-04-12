// ViewModel/ProductViewModelFactory.kt
package com.example.nomsy.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nomsy.data.repository.RecipeRepository
import com.example.nomsy.viewmodel.recipeViewModel

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return recipeViewModel(repository) as T
    }
}

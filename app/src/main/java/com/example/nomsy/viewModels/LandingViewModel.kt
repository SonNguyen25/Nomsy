package com.example.nomsy.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nomsy.data.Food
import com.example.nomsy.data.Recipe
import com.example.nomsy.models.IFoodRepository
import com.example.nomsy.models.IRecipeRepository

class LandingViewModel(
    private val foodRepository: IFoodRepository,
    private val recipeRepository: IRecipeRepository
) : ViewModel() {

    private val _landingData = MutableLiveData<LandingData>()
    val landingData: LiveData<LandingData> = _landingData

    fun loadLandingData(userId: String) {
        // Example: Load both foods and recipes to display on the landing page.
        // In a real app, you might combine multiple LiveData sources using MediatorLiveData.
    }
}

data class LandingData(
    val foods: List<Food>,
    val recipes: List<Recipe>
)
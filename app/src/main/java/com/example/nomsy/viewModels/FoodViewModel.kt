package com.example.nomsy.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.data.remote.SpoonacularApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.nomsy.data.remote.MealTrackerRetrofitClient
import android.util.Log
import com.example.nomsy.data.remote.NutritionTotals

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    // LiveData to hold the recognized food name
    private val _recognizedFood = MutableLiveData<String>()
    val recognizedFood: LiveData<String> get() = _recognizedFood

    // Hold the single food detail from your API
    private val _foodDetail = MutableLiveData<Food?>()
    val foodDetail: LiveData<Food?> = _foodDetail

    val allFoods = mutableStateListOf<Food>()

    val searchResults = mutableStateListOf<Food>()

    private val _dailySummary = MutableLiveData<NutritionTotals>()
    val dailySummary: LiveData<NutritionTotals> get() = _dailySummary

    fun analyzeWithSpoonacular(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val food = SpoonacularApiService.analyzeFoodImage(bitmap)
            food?.let {
                _recognizedFood.postValue(it.food_name)
                _foodDetail.postValue(it)
            }
        }
    }

    fun searchFoodsFromApi(query: String) {
        viewModelScope.launch {
            try {
                val response = MealTrackerRetrofitClient.mealTrackerApi.getAllFoods()
                if (response.isSuccessful) {
                    searchResults.clear()
                    val foods = response.body()?.foods?.filter {
                        it.food_name.contains(query, ignoreCase = true)
                    } ?: emptyList()
                    searchResults.addAll(foods)
                }
            } catch (e: Exception) {
                Log.e("FoodViewModel", "Search API failed: ${e.message}")
            }
        }
    }

    fun fetchAllFoods() {
        viewModelScope.launch {
            try {
                val response = MealTrackerRetrofitClient.mealTrackerApi.getAllFoods()
                if (response.isSuccessful) {
                    val foods = response.body()?.foods ?: emptyList()
                    allFoods.clear()
                    allFoods.addAll(foods)
                    searchResults.clear()
                    searchResults.addAll(foods)
                } else {
                    Log.e("FoodViewModel", "Failed to fetch foods: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FoodViewModel", "Exception during fetchAllFoods: ${e.message}")
            }
        }
    }

    fun fetchDailySummary(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = MealTrackerRetrofitClient.mealTrackerApi.getDailySummary(date)
                if (response.isSuccessful) {
                    _dailySummary.postValue(response.body()?.totals)
                } else {
                    Log.e("FoodViewModel", "Daily summary fetch error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FoodViewModel", "Exception during daily summary fetch: ${e.message}")
            }
        }
    }


}
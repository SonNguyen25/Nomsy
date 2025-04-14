package com.example.nomsy.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nomsy.data.local.MealTrackerDatabase
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.models.FoodLog
import com.example.nomsy.data.remote.DailySummaryResponse
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.remote.MealTrackerApiService
import com.example.nomsy.data.remote.MealTrackerRetrofitClient
import com.example.nomsy.data.repository.MealTrackerRepository
import com.example.nomsy.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.log


class
HomeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database = MealTrackerDatabase.getInstance(application)
    private val mealDao = database.mealDao()
    private val apiService = MealTrackerRetrofitClient.mealTrackerApi
    private val mealRepository = MealTrackerRepository(apiService, mealDao)

    // Nutrition totals
    val nutritionTotals = MutableLiveData<Result<DailySummaryEntity?>>()

    // Meals by type
    val mealsByType = MutableLiveData<Result<Map<String, List<MealItem>>>>()

    // Water intake dont think we need this since its in nutrition totals
    private val _waterIntake = MutableStateFlow(0.0)
    val waterIntake: StateFlow<Double> = _waterIntake


    /**
     * Using an integer to keep track of date. This is simplified because
     * our dummy data only goes from 4/11- 4/13
     * **/
    // simplify the date to always start at 12
    val selectedDate = MutableStateFlow(12)

    init {
        loadData()
        loadWaterIntake("2025-04-${selectedDate.value}")
    }

    // Set date and load data for that date
    // simplify this to be 2025-04-[date]
    fun setDate(date: Int) {
        selectedDate.value = date
        loadData()
    }

    private fun loadData() {
        //our api takes YYYY-MM-DD format
        loadDataForDate("2025-04-${selectedDate.value}")
    }

    // Load all data for the current date
    private fun loadDataForDate(date: String) {
        loadNutritionTotals(date)
        loadMealsByType(date)
    }

    private fun loadMealsByType(date: String) {
        viewModelScope.launch {
            mealsByType.value = Result.Loading
            // Fetch meals by type
            when (val result = mealRepository.getMealsByDate(date)) {
                is Result.Success -> mealsByType.value = Result.Success(result.data)
                is Result.Error -> mealsByType.value = Result.Error(result.exception)
                else -> mealsByType.value = Result.Loading
            }
        }

    }

    // Load nutrition totals
    private fun loadNutritionTotals(date: String) {
        viewModelScope.launch {
            nutritionTotals.value = Result.Loading
            // fetch
            when (val result = mealRepository.getDailyNutritionTotals(date)) {
                is Result.Success -> {
                    result.data.collect { summaryEntity ->
                        nutritionTotals.value = Result.Success(summaryEntity)
                    }
                }

                is Result.Error -> nutritionTotals.value = Result.Error(result.exception)
                else -> nutritionTotals.value = Result.Loading
            }
        }
    }

    private fun loadWaterIntake(date: String) {
        viewModelScope.launch {
            nutritionTotals.value = Result.Loading
            // fetch
            when (val result = mealRepository.getDailyNutritionTotals(date)) {
                is Result.Success -> {
                    result.data.collect { summaryEntity ->
                        if (summaryEntity != null) {
                            _waterIntake.value = summaryEntity.waterLiters
                        }
                    }
                }

                is Result.Error -> nutritionTotals.value = Result.Error(result.exception)
                else -> nutritionTotals.value = Result.Loading
            }
        }
    }

    // only allow 4/11, 4/12, 4/13 because that is our dummy data.
    fun incrementDate() {
        if (selectedDate.value <= 12) {
            selectedDate.value += 1
        }
        loadData()
    }

    fun decrementDate() {
        if (selectedDate.value >= 12) {
            selectedDate.value -= 1
        }
        loadData()
    }

    fun updateWaterIntake(date: String, newWaterIntake: Double) {
        viewModelScope.launch {
            _waterIntake.value = newWaterIntake
            mealRepository.updateWaterIntake(date, newWaterIntake)
        }
    }

    fun deleteMeal(date: String, foodName: String) {
        viewModelScope.launch {
            mealRepository.deleteMeal(date, foodName)
        }
        loadData()
    }
}
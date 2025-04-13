package com.example.nomsy.viewModels

import android.app.Application
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


<<<<<<< Updated upstream
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    sealed class HomeState {
        object Loading : HomeState()
        data class Success(
            val calories: Int,
            val protein: Int,
            val carbs: Int,
            val fat: Int,
            val water: Double,
            val calorieGoal: Int = 2000, // Default goals
            val proteinGoal: Int = 150,
            val carbsGoal: Int = 250,
            val fatGoal: Int = 70,
            val waterGoal: Double = 2.5,
            val breakfastMeals: List<MealItem> = emptyList(),
            val lunchMeals: List<MealItem> = emptyList(),
            val dinnerMeals: List<MealItem> = emptyList()
        ) : HomeState() {
            // Helper properties for UI
            val caloriePercent: Float get() = (calories.toFloat() / calorieGoal).coerceIn(0f, 1f)
            val proteinPercent: Float get() = (protein.toFloat() / proteinGoal).coerceIn(0f, 1f)
            val carbsPercent: Float get() = (carbs.toFloat() / carbsGoal).coerceIn(0f, 1f)
            val fatPercent: Float get() = (fat.toFloat() / fatGoal).coerceIn(0f, 1f)
            val waterPercent: Float get() = (water / waterGoal).coerceIn(0.0, 1.0).toFloat()
        }
=======
class
HomeViewModel(application: Application) :
    AndroidViewModel(application) {
    private val database = MealTrackerDatabase.getInstance(application)
    private val mealDao = database.mealDao()
    private val apiService = MealTrackerRetrofitClient.mealTrackerApi
    private val mealRepository = MealTrackerRepository(apiService, mealDao)
>>>>>>> Stashed changes

    // Nutrition totals
    private val _nutritionTotals = MutableLiveData<Result<DailySummaryEntity?>>()
    val nutritionTotals: LiveData<Result<DailySummaryEntity?>> = _nutritionTotals

    // Water intake dont think we need this since its in nutrition totals
    private val _waterIntake = MutableStateFlow(0.0)
    val waterIntake: StateFlow<Double> = _waterIntake

    // Meals by type
    private val _mealsByType = MutableLiveData<Result<Map<String, List<MealItem>>>>()

    /**
     * Using an integer to keep track of date. This is simplified because
     * our dummy data only goes from 4/11- 4/13
     * **/
    // simplify the date to always start at 12
    val selectedDate = MutableStateFlow(12)

    init {
        loadData()
    }

    // Set date and load data for that date
    // simplify this to be 2025-04-[date]
    fun setDate(date: Int) {
        selectedDate.value = date
        loadData()
    }

    private fun loadData() {
        //our api takes YYYY-MM-DD format
        loadDataForDate("2025-04-$selectedDate")
    }

    // Load all data for the current date
    private fun loadDataForDate(date: String) {
        loadNutritionTotals(date)
        loadMealsByType(date)
    }

    private fun loadMealsByType(date: String) {
        viewModelScope.launch {
            _mealsByType.value = Result.Loading
            // Fetch meals by type
            when (val result = mealRepository.getMealsByDate(date)) {
                is Result.Success -> _mealsByType.value = Result.Success(result.data)
                is Result.Error -> _mealsByType.value = Result.Error(result.exception)
                else -> _mealsByType.value = Result.Loading
            }
        }

    }

    // Load nutrition totals
    private fun loadNutritionTotals(date: String) {
        viewModelScope.launch {
            _nutritionTotals.value = Result.Loading
            // fetch
            when (val result = mealRepository.getDailyNutritionTotals(date)) {
                is Result.Success -> {
                    result.data.collect { summaryEntity ->
                        _nutritionTotals.value = Result.Success(summaryEntity)
                        if (summaryEntity != null) {
                            _waterIntake.value = summaryEntity.waterLiters
                        }
                    }
                }

                is Result.Error -> _nutritionTotals.value = Result.Error(result.exception)
                else -> _nutritionTotals.value = Result.Loading
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
            loadNutritionTotals(date)
        }
    }


}
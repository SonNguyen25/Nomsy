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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


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

    // Formatted date for API calls
    val formattedDate: Flow<String> = selectedDate.map { "2025-04-$it" }

    // Track current loading jobs
    private var nutritionLoadingJob: Job? = null
    private var mealsLoadingJob: Job? = null

    init {
        // React to date changes automatically
        viewModelScope.launch {
            formattedDate
                .distinctUntilChanged()
                .collect { date ->
                    Log.d("HomeViewModel", "Loading data for date: $date")
                    loadDataForDate(date)
                }
        }
        updateWaterFromNutrition("2025-04-${selectedDate.value}")
    }


    // Set date and load data for that date
    // simplify this to be 2025-04-[date]
    fun setDate(date: Int) {
        selectedDate.value = date
    }


    // Load all data for the current date
    private fun loadDataForDate(date: String) {
        Log.d("HomeViewModel", "loadDataForDate called with date: $date")
        loadNutritionTotals(date)
        loadMealsByType(date)
        updateWaterFromNutrition(date)
    }

    private fun loadMealsByType(date: String) {
        mealsLoadingJob?.cancel()
        mealsLoadingJob = viewModelScope.launch {
            try {
                mealsByType.value = Result.Loading
                Log.d("HomeViewModel", "Loading meals for date: $date")

                // Fetch meals by type
                val result = mealRepository.getMealsByDate(date)
                mealsByType.value = result

                if (result is Result.Success) {
                    Log.d("HomeViewModel", "Loaded ${result.data.size} meal types")
                } else if (result is Result.Error) {
                    Log.e("HomeViewModel", "Error loading meals", result.exception)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception in loadMealsByType", e)
                mealsByType.value = Result.Error(e)
            }
        }

    }

    // Load nutrition totals
    private fun loadNutritionTotals(date: String) {
        nutritionLoadingJob?.cancel()
        nutritionLoadingJob = viewModelScope.launch {
            nutritionTotals.value = Result.Loading
            when (val result = mealRepository.getDailyNutritionTotals(date)) {
                is Result.Success -> {
                    result.data.take(1).collect { summaryEntity ->
                        nutritionTotals.value = Result.Success(summaryEntity)
                    }
                }

                is Result.Error -> nutritionTotals.value = Result.Error(result.exception)
                else -> nutritionTotals.value = Result.Loading
            }
        }
    }

    private fun updateWaterFromNutrition(date: String) {
        viewModelScope.launch {
            try {
                val result = mealRepository.getDailyNutritionTotals(date)
                if (result is Result.Success) {
                    result.data.take(1).collect { summaryEntity ->
                        if (summaryEntity != null) {
                            _waterIntake.value = summaryEntity.waterLiters
                            Log.d(
                                "HomeViewModel",
                                "Water intake updated to: ${summaryEntity.waterLiters}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error updating water intake", e)
            }
        }
    }

    fun updateWaterIntake(date: String, newWaterIntake: Double) {
        viewModelScope.launch {
            val roundedNewWaterIntake = (Math.round(newWaterIntake * 10) / 10.0)
            // calculate delta (difference between current and new) api takes dif instead of new val
            val delta = roundedNewWaterIntake - _waterIntake.value
            _waterIntake.value = roundedNewWaterIntake / 10.0
            val updatedTotal =
                mealRepository.updateWaterIntakeDelta(date, delta, roundedNewWaterIntake)
            _waterIntake.value = updatedTotal
        }
    }


    // only allow 4/11, 4/12, 4/13 because that is our dummy data.
    fun incrementDate() {
        if (selectedDate.value < 13) {
            selectedDate.value += 1
        }
    }

    fun decrementDate() {
        if (selectedDate.value > 11) {
            selectedDate.value -= 1
        }
    }


    fun deleteMeal(date: String, foodName: String) {
        viewModelScope.launch {
            mealRepository.deleteMeal(date, foodName)
            delay(200) // small delay to allow database update to complete
            loadDataForDate(date)
        }
    }
}
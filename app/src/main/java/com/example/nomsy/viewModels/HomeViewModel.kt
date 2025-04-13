package com.example.nomsy.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.FoodLog
import com.example.nomsy.data.remote.DailySummaryResponse
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.repository.MealTrackerRepository

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class
HomeViewModel(application: Application) : AndroidViewModel(application) {
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

        data class Error(val message: String) : HomeState()
    }

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state
    private val userDatabase = UserDatabase.getDatabase(application)
    private val authViewModel = AuthViewModel(application)
    private val repository = MealTrackerRepository(
        apiService = TODO(),
        context = TODO()
    )

    private val _foodLogs = MutableStateFlow<List<FoodLog>>(emptyList())

    /**
     * Using an integer to keep track of date. This is simplified because
     * our dummy data only goes from 4/11- 4/13
     * **/
    // simplify the date to always start at 12
    val selectedDate = MutableStateFlow(12)


    init {
        loadHomeScreenData()
    }

    private fun loadHomeScreenData() {
        //our api takes YYYY-MM-DD format
        loadDataForDate("2025-04-$selectedDate")
    }

    private fun loadDataForDate(dateString: String) {
        viewModelScope.launch {
            _state.value = HomeState.Loading


            repository.getDailySummary(dateString).collect { result ->
//                when (result) {
//                    is Result.Loading -> {
//                        // Already set loading above
//                    }
//                    is Result.Success -> {
//                        processDailySummary(result.data)
//                    }
//                    is Result.Error -> {
//                        _state.value = HomeState.Error(result.exception.message ?: "Unknown error")
//                    }
//
//                    is com.example.nomsy.utils.Result.Error -> TODO()
//                    com.example.nomsy.utils.Result.Loading -> TODO()
//                    is com.example.nomsy.utils.Result.Success -> TODO()
//                }
            }
        }
    }

    private fun processDailySummary(data: DailySummaryResponse) {
        // Process meals by type
        val breakfastMeals = data.meals["breakfast"] ?: emptyList()
        val lunchMeals = data.meals["lunch"] ?: emptyList()
        val dinnerMeals = data.meals["dinner"] ?: emptyList()

        _state.value = HomeState.Success(
            calories = data.totals.calories,
            protein = data.totals.protein,
            carbs = data.totals.carbs,
            fat = data.totals.fat,
            water = data.totals.water,
            // Use default goals (these could come from user preferences in a real app)
            breakfastMeals = breakfastMeals,
            lunchMeals = lunchMeals,
            dinnerMeals = dinnerMeals
        )
    }


    // only allow 4/11, 4/12, 4/13 because that is our dummy data.
    fun incrementDate() {
        if (selectedDate.value <= 12) {
            selectedDate.value += 1
        }
        loadHomeScreenData()
    }

    fun decrementDate() {
        if (selectedDate.value >= 12) {
            selectedDate.value -= 1
        }
        loadHomeScreenData()
    }

    fun updateWaterIntake(amount: Float) {
        viewModelScope.launch {
            try {

                // Data will refresh through flows
            } catch (e: Exception) {

            }
        }
    }

    fun deleteFoodLog(id: String) {
        viewModelScope.launch {
            try {

                // Data will refresh through flows
            } catch (e: Exception) {

            }
        }
    }

}
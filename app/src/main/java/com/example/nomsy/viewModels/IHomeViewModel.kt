package com.example.nomsy.viewModels

import androidx.lifecycle.LiveData
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.utils.Result
import kotlinx.coroutines.flow.StateFlow


interface HomeViewModelInterface {
    val nutritionTotals: LiveData<Result<DailySummaryEntity?>>
    val mealsByType: LiveData<Result<Map<String, List<MealItem>>>>
    val waterIntake: StateFlow<Double>
    val selectedDate: StateFlow<Int>
    
    fun incrementDate()

    fun decrementDate()

    fun updateWaterIntake(date: String, newWaterIntake: Double)

    fun deleteMeal(date: String, foodName: String)
}
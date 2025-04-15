package com.example.nomsy.viewModels

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.data.remote.AddMealResponse
import com.example.nomsy.data.remote.NutritionTotals
import com.example.nomsy.utils.Result

interface IFoodViewModel {
    val recognizedFood: LiveData<String>
    val foodDetail: LiveData<Food?>
    val allFoods: SnapshotStateList<Food>
    val searchResults: SnapshotStateList<Food>
    val mealResult: LiveData<Result<AddMealResponse>?>
    val dailySummary: LiveData<NutritionTotals?>

    fun analyzeWithSpoonacular(bitmap: Bitmap)
    fun searchFoodsFromApi(query: String)
    fun fetchAllFoods()
    fun fetchDailySummary(date: String)
    fun submitMeal(request: AddMealRequest)
    fun clearMealResult()
}

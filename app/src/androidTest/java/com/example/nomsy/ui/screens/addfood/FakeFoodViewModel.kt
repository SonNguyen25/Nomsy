package com.example.nomsy.ui.screens.addfood
import androidx.compose.runtime.mutableStateListOf
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.viewModels.FoodViewModel
import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.data.remote.AddMealResponse
import com.example.nomsy.data.remote.NutritionTotals
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IFoodViewModel

class FakeFoodViewModel : IFoodViewModel {
    var submittedMealRequest: AddMealRequest? = null
    var clearMealResultCalled = false

    override val recognizedFood = MutableLiveData("")
    override val foodDetail = MutableLiveData<Food?>()
    override val allFoods = mutableStateListOf<Food>()
    override val searchResults = mutableStateListOf<Food>()
    override val mealResult = MutableLiveData<Result<AddMealResponse>?>()
    override val dailySummary = MutableLiveData<NutritionTotals?>()

    override fun analyzeWithSpoonacular(bitmap: Bitmap) {}
    override fun searchFoodsFromApi(query: String) {}
    override fun fetchAllFoods() {}
    override fun fetchDailySummary(date: String) {}

    override fun submitMeal(request: AddMealRequest) {
        submittedMealRequest = request
    }

    override fun clearMealResult() {
        clearMealResultCalled = true
    }
}


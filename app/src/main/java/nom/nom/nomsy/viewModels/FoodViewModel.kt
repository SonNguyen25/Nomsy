package nom.nom.nomsy.viewModels

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import nom.nom.nomsy.data.local.entities.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import nom.nom.nomsy.data.remote.*
import nom.nom.nomsy.data.repository.FoodRepository
import nom.nom.nomsy.utils.Result

class FoodViewModel(application: Application) : AndroidViewModel(application), IFoodViewModel {
    // LiveData to hold the recognized food name
    private val _recognizedFood = MutableLiveData<String>()
    override val recognizedFood: LiveData<String> get() = _recognizedFood

    // Hold the single food detail from your API
    private val _foodDetail = MutableLiveData<Food?>()
    override val foodDetail: LiveData<Food?> = _foodDetail

    override val allFoods = mutableStateListOf<Food>()

    override val searchResults = mutableStateListOf<Food>()

    private val _mealResult = MutableLiveData<Result<AddMealResponse>>()
    override val mealResult: LiveData<Result<AddMealResponse>?> get() = _mealResult


    private val _dailySummary = MutableLiveData<NutritionTotals>()
    override val dailySummary: LiveData<NutritionTotals?> get() = _dailySummary

    override fun analyzeWithSpoonacular(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val food = SpoonacularApiService.analyzeFoodImage(bitmap)
            food?.let {
                _recognizedFood.postValue(it.food_name)
                _foodDetail.postValue(it)
            }
        }
    }

    override fun searchFoodsFromApi(query: String) {
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

    override fun fetchAllFoods() {
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

    override fun fetchDailySummary(date: String) {
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

    // Adding a meal for the Database
    override fun submitMeal(request: AddMealRequest) {
        viewModelScope.launch {
            FoodRepository().addMeal(request).observeForever {
                _mealResult.value = it
            }
        }
    }

    //Clear the stored value
    override fun clearMealResult() {
        _mealResult.value = null
    }
}
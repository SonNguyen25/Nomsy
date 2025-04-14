package com.example.nomsy.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.data.remote.SpoonacularApiService
import com.example.nomsy.data.repository.AuthRepository
import com.example.nomsy.data.repository.FoodRepository
import com.example.nomsy.data.repository.IFoodRepository
import com.example.nomsy.data.repository.IUserRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {
//    private val userDatabase = UserDatabase.getDatabase(application)
    private val foodRepository: IFoodRepository = FoodRepository()


    private val _foods = MutableLiveData<Result<List<Food>>>()
    val foods: LiveData<Result<List<Food>>> = _foods

    // LiveData to hold the recognized food name from ML Kit.
    private val _recognizedFood = MutableLiveData<String>()
    val recognizedFood: LiveData<String> get() = _recognizedFood

    // Hold the single food detail from your API
    private val _foodDetail = MutableLiveData<Food?>()
    val foodDetail: LiveData<Food?> = _foodDetail

    // Processes an image using Google ML Kit and updates the recognized food name.
    fun processFoodImage(context: Context, bitmap: Bitmap) {
        // Create an InputImage from the bitmap.
        val image = InputImage.fromBitmap(bitmap, 0)
        // Get an instance of the default image labeler.
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        // Process the image using ML Kit.
        labeler.process(image)
            .addOnSuccessListener { labels ->
                // For simplicity, take the first label as the recognized food name.
                if (labels.isNotEmpty()) {
                    _recognizedFood.value = labels.first().text
                    // Optionally, trigger a call to your nutrition API here using the food name.
                }
            }
            .addOnFailureListener { exception ->
                // Log the exception or handle error as necessary.
                exception.printStackTrace()
            }
    }

    fun addFood(food: Food) {
        // This can be expanded to update the UI based on addFood result if needed.
        foodRepository.addFood(food)
    }

    fun loadFoods(userId: String) {
        _foods.value = Result.loading()
        foodRepository.getFoods(userId).observeForever {
//            _foods.value = it
        }
    }

    fun searchFood(name: String) {
        // TODO: call your backend API to look up 'name' and post to _foodDetail
        // e.g. repository.searchFood(name).observeForever { _foodDetail.postValue(it.data) }
    }

    fun analyzeWithSpoonacular(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val food = SpoonacularApiService.analyzeFoodImage(bitmap)
            food?.let {
                _recognizedFood.postValue(it.food_name)
                _foodDetail.postValue(it)
            }
        }
    }


}
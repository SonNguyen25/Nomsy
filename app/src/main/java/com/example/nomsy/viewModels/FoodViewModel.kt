package com.example.nomsy.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nomsy.models.Food
import com.example.nomsy.models.IFoodRepository

class FoodViewModel(private val foodRepository: IFoodRepository) : ViewModel() {

    private val _foods = MutableLiveData<Result<List<Food>>>()
    val foods: LiveData<Result<List<Food>>> = _foods

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
}
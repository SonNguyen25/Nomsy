package com.example.nomsy.data.repository

import androidx.lifecycle.LiveData
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.utils.Result

class FoodRepository : IFoodRepository {
    override fun addFood(food: Food): LiveData<Result<Food>> {
        TODO("Not yet implemented")
    }

    override fun getFoods(userId: String): LiveData<Result<List<Food>>> {
        TODO("Not yet implemented")
    }
}
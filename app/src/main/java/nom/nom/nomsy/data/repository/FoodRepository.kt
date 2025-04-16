package nom.nom.nomsy.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import nom.nom.nomsy.data.remote.AddMealRequest
import nom.nom.nomsy.data.remote.AddMealResponse
import nom.nom.nomsy.data.remote.MealTrackerRetrofitClient
import nom.nom.nomsy.utils.Result

class FoodRepository : IFoodRepository {
    override fun addMeal(mealRequest: AddMealRequest): LiveData<Result<AddMealResponse>> = liveData {
        emit(Result.loading())
        try {
            val response = MealTrackerRetrofitClient.mealTrackerApi.addMeal(mealRequest)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error(Exception("Failed to add meal: ${response.errorBody()?.string()}")) as Result<AddMealResponse>)
            }
        } catch (e: Exception) {
            emit(Result.Error(e) as Result<AddMealResponse>)
        }
    }
}

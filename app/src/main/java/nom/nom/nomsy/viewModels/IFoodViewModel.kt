package nom.nom.nomsy.viewModels

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import nom.nom.nomsy.data.local.entities.Food
import nom.nom.nomsy.data.remote.AddMealRequest
import nom.nom.nomsy.data.remote.AddMealResponse
import nom.nom.nomsy.data.remote.NutritionTotals
import nom.nom.nomsy.utils.Result

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

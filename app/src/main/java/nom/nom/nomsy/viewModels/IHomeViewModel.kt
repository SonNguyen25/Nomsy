package nom.nom.nomsy.viewModels

import androidx.lifecycle.LiveData
import nom.nom.nomsy.data.local.entities.DailySummaryEntity
import nom.nom.nomsy.data.remote.MealItem
import nom.nom.nomsy.utils.Result
import kotlinx.coroutines.flow.StateFlow


interface IHomeViewModel {
    val nutritionTotals: LiveData<Result<DailySummaryEntity?>>
    val mealsByType: LiveData<Result<Map<String, List<MealItem>>>>
    val waterIntake: StateFlow<Double>
    val selectedDate: StateFlow<Int>
    fun incrementDate()
    fun decrementDate()
    fun updateWaterIntake(date: String, newWaterIntake: Double)
    fun deleteMeal(date: String, foodName: String)
    fun refreshData()
}
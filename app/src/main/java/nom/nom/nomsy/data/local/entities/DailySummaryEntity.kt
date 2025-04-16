package nom.nom.nomsy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import nom.nom.nomsy.data.remote.MealItem

@Entity(tableName = "daily_summaries")
data class DailySummaryEntity(
    @PrimaryKey val date: String,
    val totalCalories: Int,
    val totalCarbs: Int,
    val totalProtein: Int,
    val totalFat: Int,
    val waterLiters: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

// model
data class DailySummary(
    val date: String,
    val totalCalories: Int,
    val totalCarbs: Int,
    val totalProtein: Int,
    val totalFat: Int,
    val waterLiters: Double,
    val mealsByType: Map<String, List<MealItem>>
)

// entity to model
fun DailySummaryEntity.toDomainModel(meals: Map<String, List<MealEntity>>): DailySummary {
    val mealsByType = meals.mapValues { (_, mealEntities) ->
        mealEntities.map { it.toMealItem() }
    }

    return DailySummary(
        date = this.date,
        totalCalories = this.totalCalories,
        totalCarbs = this.totalCarbs,
        totalProtein = this.totalProtein,
        totalFat = this.totalFat,
        waterLiters = this.waterLiters,
        mealsByType = mealsByType
    )
}
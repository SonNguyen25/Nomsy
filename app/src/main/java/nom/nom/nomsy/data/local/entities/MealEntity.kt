package nom.nom.nomsy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import nom.nom.nomsy.data.remote.MealItem

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealId: String,
    val date: String,
    val mealType: String,
    val food_name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val time: Long = System.currentTimeMillis()
)

// Convert MealEntity to MealItem
fun MealEntity.toMealItem(): MealItem {
    return MealItem(
        food_name = this.food_name,
        calories = this.calories,
        carbs = this.carbs,
        protein = this.protein,
        fat = this.fat
    )
}
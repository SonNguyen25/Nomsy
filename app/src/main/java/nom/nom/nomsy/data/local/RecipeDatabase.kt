package nom.nom.nomsy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nom.nom.nomsy.data.local.dao.RecipeDAO
import nom.nom.nomsy.data.local.entities.Recipe

@Database(entities = [Recipe::class], version = 1)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDAO(): RecipeDAO

    companion object {
        @Volatile private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

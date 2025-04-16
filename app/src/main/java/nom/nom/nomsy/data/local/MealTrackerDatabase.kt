package nom.nom.nomsy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import nom.nom.nomsy.data.local.dao.MealTrackerDao
import nom.nom.nomsy.data.local.entities.DailySummaryEntity
import nom.nom.nomsy.data.local.entities.MealEntity

@Database(
    entities = [MealEntity::class, DailySummaryEntity::class],
    version = 3,
    exportSchema = false
)
abstract class MealTrackerDatabase : RoomDatabase() {
    abstract fun mealDao(): MealTrackerDao

    companion object {
        @Volatile
        private var INSTANCE: MealTrackerDatabase? = null

        fun getInstance(context: Context): MealTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealTrackerDatabase::class.java,
                    "meal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }

}
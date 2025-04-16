package nom.nom.nomsy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nom.nom.nomsy.data.local.dao.UserDao
import nom.nom.nomsy.data.local.entities.User

@Database(entities = [User::class], version = 2)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var instance: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "nomsy_database"
                ).fallbackToDestructiveMigration()
                    .build().also { instance = it }

            }
    }
}
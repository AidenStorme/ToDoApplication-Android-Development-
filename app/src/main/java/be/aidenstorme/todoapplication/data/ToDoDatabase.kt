package be.aidenstorme.todoapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, ToDo::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun toDoDao(): ToDoDao

    companion object {
        @Volatile
        private var Instance: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                )
                .addCallback(ToDoDatabaseCallback(context))
                .build()
                .also { Instance = it }
            }
        }
    }

    private class ToDoDatabaseCallback(
        private val context: Context
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                database.userDao().insertUsers(Datasource.getUsers())
                database.toDoDao().insertToDos(Datasource.getToDos())
            }
        }
    }
}

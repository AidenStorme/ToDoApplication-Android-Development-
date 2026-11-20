package be.aidenstorme.todoapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User

@Database(entities = [User::class, ToDo::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun toDoDao(): ToDoDao
}

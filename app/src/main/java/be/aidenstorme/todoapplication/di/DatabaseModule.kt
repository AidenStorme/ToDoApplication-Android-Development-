package be.aidenstorme.todoapplication.di

import android.content.Context
import androidx.room.Room
import be.aidenstorme.todoapplication.data.ToDoDao
import be.aidenstorme.todoapplication.data.ToDoDatabase
import be.aidenstorme.todoapplication.data.ToDoDatabaseCallback
import be.aidenstorme.todoapplication.data.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "todo_database"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: ToDoDatabaseCallback
    ): ToDoDatabase =
        Room.databaseBuilder(context, ToDoDatabase::class.java, DATABASE_NAME)
            .addCallback(callback)
            .build()

    @Provides
    fun provideUserDao(database: ToDoDatabase): UserDao = database.userDao()

    @Provides
    fun provideToDoDao(database: ToDoDatabase): ToDoDao = database.toDoDao()
}

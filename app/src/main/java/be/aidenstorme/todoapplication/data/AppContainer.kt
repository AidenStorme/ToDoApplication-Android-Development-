package be.aidenstorme.todoapplication.data

import android.content.Context

interface AppContainer {
    val userRepository: UserRepository
    val toDoRepository: ToDoRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(ToDoDatabase.getDatabase(context).userDao())
    }

    override val toDoRepository: ToDoRepository by lazy {
        OfflineToDoRepository(ToDoDatabase.getDatabase(context).toDoDao())
    }
}

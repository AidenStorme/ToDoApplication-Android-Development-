package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.ToDo
import kotlinx.coroutines.flow.Flow

interface ToDoRepository {
    fun getAllToDos(): Flow<List<ToDo>>
    fun getToDoByNumber(number: Int): Flow<ToDo?>
    suspend fun insertToDo(toDo: ToDo): Long
    suspend fun updateToDo(toDo: ToDo)
    suspend fun deleteToDo(toDo: ToDo)
}

class OfflineToDoRepository(private val toDoDao: ToDoDao) : ToDoRepository {
    override fun getAllToDos(): Flow<List<ToDo>> = toDoDao.getAllToDos()
    override fun getToDoByNumber(number: Int): Flow<ToDo?> = toDoDao.getToDoByNumber(number)
    override suspend fun insertToDo(toDo: ToDo): Long = toDoDao.insertToDo(toDo)
    override suspend fun updateToDo(toDo: ToDo) = toDoDao.updateToDo(toDo)
    override suspend fun deleteToDo(toDo: ToDo) = toDoDao.deleteToDo(toDo)
}

package be.aidenstorme.todoapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import be.aidenstorme.todoapplication.models.ToDo
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todos ORDER BY number ASC")
    fun getAllToDos(): Flow<List<ToDo>>

    @Query("SELECT * FROM todos WHERE number = :number")
    fun getToDoByNumber(number: Int): Flow<ToDo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(toDo: ToDo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDos(toDos: List<ToDo>)

    @Update
    suspend fun updateToDo(toDo: ToDo)

    @Delete
    suspend fun deleteToDo(toDo: ToDo)
}

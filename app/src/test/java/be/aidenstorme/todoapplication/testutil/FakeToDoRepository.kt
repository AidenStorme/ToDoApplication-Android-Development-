package be.aidenstorme.todoapplication.testutil

import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.models.ToDo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory ToDoRepository voor ViewModel-tests (geen database nodig). */
class FakeToDoRepository(initialToDos: List<ToDo> = emptyList()) : ToDoRepository {

    private val todos = MutableStateFlow(initialToDos)

    /** Huidige inhoud, handig om in een test te controleren wat er opgeslagen werd. */
    val currentToDos: List<ToDo> get() = todos.value

    override fun getAllToDos(): Flow<List<ToDo>> = todos

    override fun getToDoByNumber(number: Int): Flow<ToDo?> =
        todos.map { list -> list.find { it.number == number } }

    override suspend fun insertToDo(toDo: ToDo): Long {
        // Net als Room met autoGenerate: nummer 0 krijgt het volgende vrije nummer
        val number = if (toDo.number == 0) (todos.value.maxOfOrNull { it.number } ?: 0) + 1 else toDo.number
        todos.update { it + toDo.copy(number = number) }
        return number.toLong()
    }

    override suspend fun updateToDo(toDo: ToDo) {
        todos.update { list -> list.map { if (it.number == toDo.number) toDo else it } }
    }

    override suspend fun deleteToDo(toDo: ToDo) {
        todos.update { list -> list.filterNot { it.number == toDo.number } }
    }
}

package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User

data class ToDoListUiState(
    val toDos: List<ToDo> = emptyList(),
    val users: List<User> = emptyList()
)

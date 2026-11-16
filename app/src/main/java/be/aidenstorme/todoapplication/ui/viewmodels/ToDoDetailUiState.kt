package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User

data class ToDoDetailUiState(
    val toDo: ToDo? = null,
    val assignedUser: User? = null,
    val createdByUser: User? = null,
    val users: List<User> = emptyList()
)

package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User

data class ToDoEditUiState(
    val toDoToEdit: ToDo? = null,
    val title: String = "",
    val description: String = "",
    val assignedUser: User? = null,
    val analysisDone: Boolean = false,
    val developmentDone: Boolean = false,
    val reviewAndTestingDone: Boolean = false,
    val acceptanceDone: Boolean = false,
    val users: List<User> = emptyList()
)

package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.models.ToDo

data class ToDoListUiState(
    val toDos: List<ToDo> = emptyList()
)

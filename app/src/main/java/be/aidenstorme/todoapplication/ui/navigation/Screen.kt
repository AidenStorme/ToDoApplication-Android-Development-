package be.aidenstorme.todoapplication.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object ToDoList : Screen

    @Serializable
    data class ToDoDetail(val todoNumber: Int) : Screen

    @Serializable
    data class ToDoEdit(val todoNumber: Int?) : Screen
}

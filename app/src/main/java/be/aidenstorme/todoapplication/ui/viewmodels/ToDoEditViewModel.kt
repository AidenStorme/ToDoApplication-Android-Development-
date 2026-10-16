package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import be.aidenstorme.todoapplication.data.Datasource
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ToDoEditViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoEditUiState())
    val uiState: StateFlow<ToDoEditUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        _uiState.update { it.copy(users = Datasource.getUsers()) }
    }

    fun setToDoToEdit(toDo: ToDo?) {
        val currentUsers = _uiState.value.users
        if (toDo != null) {
            _uiState.value = ToDoEditUiState(
                toDoToEdit = toDo,
                title = toDo.title,
                description = toDo.description,
                assignedUser = toDo.assignedToUser,
                analysisDone = toDo.analysisDone,
                developmentDone = toDo.developmentDone,
                reviewAndTestingDone = toDo.reviewAndTestingDone,
                acceptanceDone = toDo.acceptanceDone,
                users = currentUsers
            )
        } else {
            _uiState.value = ToDoEditUiState(
                toDoToEdit = null,
                title = "",
                description = "",
                assignedUser = null,
                analysisDone = false,
                developmentDone = false,
                reviewAndTestingDone = false,
                acceptanceDone = false,
                users = currentUsers
            )
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onAssignedUserChange(user: User?) {
        _uiState.update { it.copy(assignedUser = user) }
    }

    fun onAnalysisDoneChange(done: Boolean) {
        _uiState.update { it.copy(analysisDone = done) }
    }

    fun onDevelopmentDoneChange(done: Boolean) {
        _uiState.update { it.copy(developmentDone = done) }
    }

    fun onReviewAndTestingDoneChange(done: Boolean) {
        _uiState.update { it.copy(reviewAndTestingDone = done) }
    }

    fun onAcceptanceDoneChange(done: Boolean) {
        _uiState.update { it.copy(acceptanceDone = done) }
    }

    fun onSave(onSaveCallback: (ToDo) -> Unit) {
        val state = _uiState.value
        val existing = state.toDoToEdit
        val updatedToDo = existing?.copy(
            title = state.title,
            description = state.description,
            assignedToUser = state.assignedUser,
            analysisDone = state.analysisDone,
            developmentDone = state.developmentDone,
            reviewAndTestingDone = state.reviewAndTestingDone,
            acceptanceDone = state.acceptanceDone
        ) ?: ToDo(
            number = 0,
            title = state.title,
            description = state.description,
            createdByUser = state.users.firstOrNull() ?: User(),
            assignedToUser = state.assignedUser,
            analysisDone = state.analysisDone,
            developmentDone = state.developmentDone,
            reviewAndTestingDone = state.reviewAndTestingDone,
            acceptanceDone = state.acceptanceDone
        )
        onSaveCallback(updatedToDo)
    }
}

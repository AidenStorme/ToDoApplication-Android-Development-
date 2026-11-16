package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import be.aidenstorme.todoapplication.ToDoApplication
import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.data.UserRepository
import be.aidenstorme.todoapplication.models.ToDo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ToDoDetailViewModel(
    private val toDoRepository: ToDoRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoDetailUiState())
    val uiState: StateFlow<ToDoDetailUiState> = _uiState.asStateFlow()

    fun loadToDo(todoNumber: Int) {
        combine(
            toDoRepository.getToDoByNumber(todoNumber),
            userRepository.getAllUsers()
        ) { toDo, users ->
            val assignedUser = users.find { it.id == toDo?.assignedToUserId }
            val createdByUser = users.find { it.id == toDo?.createdByUserId }
            ToDoDetailUiState(
                toDo = toDo,
                assignedUser = assignedUser,
                createdByUser = createdByUser,
                users = users
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun onAnalysisDoneChange(done: Boolean) {
        updateToDo { it.copy(analysisDone = done) }
    }

    fun onDevelopmentDoneChange(done: Boolean) {
        updateToDo { it.copy(developmentDone = done) }
    }

    fun onReviewAndTestingDoneChange(done: Boolean) {
        updateToDo { it.copy(reviewAndTestingDone = done) }
    }

    fun onAcceptanceDoneChange(done: Boolean) {
        updateToDo { currentToDo ->
            val newFinishedDate = if (done && currentToDo.assignedToUserId != null) {
                currentToDo.finishedOnDate ?: System.currentTimeMillis()
            } else {
                null
            }
            currentToDo.copy(
                acceptanceDone = done,
                finishedOnDate = newFinishedDate
            )
        }
    }

    private fun updateToDo(transform: (ToDo) -> ToDo) {
        val currentToDo = _uiState.value.toDo ?: return
        val updatedToDo = transform(currentToDo)
        viewModelScope.launch {
            toDoRepository.updateToDo(updatedToDo)
        }
    }

    companion object {
        fun factory(todoNumber: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)
                ToDoDetailViewModel(
                    toDoRepository = application.container.toDoRepository,
                    userRepository = application.container.userRepository
                ).apply {
                    loadToDo(todoNumber)
                }
            }
        }
    }
}

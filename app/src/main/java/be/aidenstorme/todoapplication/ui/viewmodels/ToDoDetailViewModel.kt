package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.data.UserRepository
import be.aidenstorme.todoapplication.models.ToDo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ToDoDetailViewModel.Factory::class)
class ToDoDetailViewModel @AssistedInject constructor(
    @Assisted todoNumber: Int,
    private val toDoRepository: ToDoRepository,
    userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoDetailUiState())
    val uiState: StateFlow<ToDoDetailUiState> = _uiState.asStateFlow()

    init {
        combine(
            toDoRepository.getToDoByNumber(todoNumber),
            userRepository.getAllUsers()
        ) { toDo, users ->
            ToDoDetailUiState(
                toDo = toDo,
                assignedUser = users.find { it.id == toDo?.assignedToUserId },
                createdByUser = users.find { it.id == toDo?.createdByUserId },
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

    @AssistedFactory
    interface Factory {
        fun create(todoNumber: Int): ToDoDetailViewModel
    }
}

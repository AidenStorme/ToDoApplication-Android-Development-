package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import be.aidenstorme.todoapplication.models.ToDo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

class ToDoDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoDetailUiState())
    val uiState: StateFlow<ToDoDetailUiState> = _uiState.asStateFlow()

    fun setToDo(toDo: ToDo) {
        _uiState.value = ToDoDetailUiState(toDo = toDo)
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
            val newFinishedDate = if (done && currentToDo.assignedToUser != null) {
                currentToDo.finishedOnDate ?: Date()
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
        _uiState.update { currentState ->
            val currentToDo = currentState.toDo ?: return@update currentState
            val updatedToDo = transform(currentToDo)
            currentState.copy(toDo = updatedToDo)
        }
    }
}

package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import be.aidenstorme.todoapplication.ToDoApplication
import be.aidenstorme.todoapplication.data.ActivityRepository
import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.data.UserRepository
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ToDoEditViewModel(
    private val toDoRepository: ToDoRepository,
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoEditUiState())
    val uiState: StateFlow<ToDoEditUiState> = _uiState.asStateFlow()

    init {
        userRepository.getAllUsers().onEach { usersList ->
            _uiState.update { currentState ->
                val currentAssigned = currentState.assignedUser
                val updatedAssigned = if (currentAssigned != null) {
                    usersList.find { it.id == currentAssigned.id } ?: currentAssigned
                } else null
                currentState.copy(users = usersList, assignedUser = updatedAssigned)
            }
        }.launchIn(viewModelScope)
    }

    fun loadToDoToEdit(todoNumber: Int?) {
        if (todoNumber == null) {
            _uiState.update {
                it.copy(
                    toDoToEdit = null,
                    title = "",
                    description = "",
                    assignedUser = null,
                    analysisDone = false,
                    developmentDone = false,
                    reviewAndTestingDone = false,
                    acceptanceDone = false
                )
            }
        } else {
            viewModelScope.launch {
                toDoRepository.getToDoByNumber(todoNumber).collect { toDo ->
                    if (toDo != null) {
                        val assigned = _uiState.value.users.find { it.id == toDo.assignedToUserId }
                        _uiState.update {
                            it.copy(
                                toDoToEdit = toDo,
                                title = toDo.title,
                                description = toDo.description,
                                assignedUser = assigned,
                                analysisDone = toDo.analysisDone,
                                developmentDone = toDo.developmentDone,
                                reviewAndTestingDone = toDo.reviewAndTestingDone,
                                acceptanceDone = toDo.acceptanceDone
                            )
                        }
                    }
                }
            }
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

    fun fetchRandomActivity() {
        _uiState.update { it.copy(activityError = null) }
        viewModelScope.launch {
            try {
                val response = activityRepository.getRandomActivity()
                _uiState.update {
                    it.copy(
                        title = response.activity,
                        description = response.type
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(activityError = ActivityError.NETWORK) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(activityError = ActivityError.SERVER) }
            }
        }
    }

    fun onSave(onSaveCallback: () -> Unit) {
        val state = _uiState.value
        val existing = state.toDoToEdit
        val creatorId = existing?.createdByUserId ?: state.users.firstOrNull()?.id ?: 1
        val updatedToDo = existing?.copy(
            title = state.title,
            description = state.description,
            assignedToUserId = state.assignedUser?.id,
            analysisDone = state.analysisDone,
            developmentDone = state.developmentDone,
            reviewAndTestingDone = state.reviewAndTestingDone,
            acceptanceDone = state.acceptanceDone
        ) ?: ToDo(
            number = 0,
            title = state.title,
            description = state.description,
            createdByUserId = creatorId,
            assignedToUserId = state.assignedUser?.id,
            analysisDone = state.analysisDone,
            developmentDone = state.developmentDone,
            reviewAndTestingDone = state.reviewAndTestingDone,
            acceptanceDone = state.acceptanceDone
        )

        viewModelScope.launch {
            if (existing != null) {
                toDoRepository.updateToDo(updatedToDo)
            } else {
                toDoRepository.insertToDo(updatedToDo)
            }
            onSaveCallback()
        }
    }

    companion object {
        fun factory(todoNumber: Int?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)
                ToDoEditViewModel(
                    toDoRepository = application.container.toDoRepository,
                    userRepository = application.container.userRepository,
                    activityRepository = application.container.activityRepository
                ).apply {
                    loadToDoToEdit(todoNumber)
                }
            }
        }
    }
}

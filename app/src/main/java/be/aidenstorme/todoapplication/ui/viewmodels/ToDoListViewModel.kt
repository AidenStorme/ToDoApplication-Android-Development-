package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import be.aidenstorme.todoapplication.ToDoApplication
import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ToDoListViewModel(
    private val toDoRepository: ToDoRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoListUiState())
    val uiState: StateFlow<ToDoListUiState> = _uiState.asStateFlow()

    init {
        combine(
            toDoRepository.getAllToDos(),
            userRepository.getAllUsers()
        ) { toDos, users ->
            ToDoListUiState(toDos = toDos, users = users)
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)
                ToDoListViewModel(
                    toDoRepository = application.container.toDoRepository,
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}

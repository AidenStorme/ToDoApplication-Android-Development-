package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ToDoListViewModel @Inject constructor(
    toDoRepository: ToDoRepository,
    userRepository: UserRepository
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
}

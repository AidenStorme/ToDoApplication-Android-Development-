package be.aidenstorme.todoapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import be.aidenstorme.todoapplication.data.Datasource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToDoListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ToDoListUiState())
    val uiState: StateFlow<ToDoListUiState> = _uiState.asStateFlow()

    init {
        loadToDos()
    }

    private fun loadToDos() {
        _uiState.value = ToDoListUiState(toDos = Datasource.getToDos())
    }
}

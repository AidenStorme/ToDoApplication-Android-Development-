package be.aidenstorme.todoapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.aidenstorme.todoapplication.data.Datasource
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoDetailViewModel
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoEditViewModel
import be.aidenstorme.todoapplication.ui.views.ToDoDetailView
import be.aidenstorme.todoapplication.ui.views.ToDoEditView
import be.aidenstorme.todoapplication.ui.views.ToDoListView

@Composable
fun ToDoNav(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navigator = remember { Navigator() }

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        modifier = modifier,
        entryProvider = { key ->
            when (key) {
                is Screen.ToDoList -> NavEntry(key) {
                    ToDoListView(
                        onToDoClick = { toDo ->
                            navigator.navigate(Screen.ToDoDetail(toDo.number))
                        },
                        onAddToDoClick = {
                            navigator.navigate(Screen.ToDoEdit(null))
                        }
                    )
                }

                is Screen.ToDoDetail -> NavEntry(key) {
                    val detailViewModel: ToDoDetailViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                val vm = ToDoDetailViewModel()
                                val toDo = Datasource.getToDos().find { it.number == key.todoNumber }
                                if (toDo != null) {
                                    vm.setToDo(toDo)
                                }
                                return vm as T
                            }
                        }
                    )
                    ToDoDetailView(
                        viewModel = detailViewModel,
                        onBack = { navigator.goBack() },
                        onEdit = {
                            navigator.navigate(Screen.ToDoEdit(key.todoNumber))
                        }
                    )
                }

                is Screen.ToDoEdit -> NavEntry(key) {
                    val editViewModel: ToDoEditViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                val vm = ToDoEditViewModel()
                                val toDo = key.todoNumber?.let { num ->
                                    Datasource.getToDos().find { it.number == num }
                                }
                                vm.setToDoToEdit(toDo)
                                return vm as T
                            }
                        }
                    )
                    ToDoEditView(
                        viewModel = editViewModel,
                        onCancel = { navigator.goBack() },
                        onSave = { updatedToDo ->
                            // Save logic: update or add in Datasource or wherever appropriate
                            // For this in-memory app, we can update or add in Datasource if needed, or just go back
                            navigator.goBack()
                        }
                    )
                }

                else -> error("Unknown screen: $key")
            }
        }
    )
}

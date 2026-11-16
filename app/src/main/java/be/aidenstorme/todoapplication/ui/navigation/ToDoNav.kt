package be.aidenstorme.todoapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoDetailViewModel
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoEditViewModel
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoListViewModel
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
                    val listViewModel: ToDoListViewModel = viewModel(factory = ToDoListViewModel.Factory)
                    ToDoListView(
                        viewModel = listViewModel,
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
                        factory = ToDoDetailViewModel.factory(key.todoNumber)
                    )
                    ToDoDetailView(
                        todoNumber = key.todoNumber,
                        viewModel = detailViewModel,
                        onBack = { navigator.goBack() },
                        onEdit = {
                            navigator.navigate(Screen.ToDoEdit(key.todoNumber))
                        }
                    )
                }

                is Screen.ToDoEdit -> NavEntry(key) {
                    val editViewModel: ToDoEditViewModel = viewModel(
                        factory = ToDoEditViewModel.factory(key.todoNumber)
                    )
                    ToDoEditView(
                        todoNumberToEdit = key.todoNumber,
                        viewModel = editViewModel,
                        onCancel = { navigator.goBack() },
                        onSave = {
                            navigator.goBack()
                        }
                    )
                }

                else -> error("Unknown screen: $key")
            }
        }
    )
}

package be.aidenstorme.todoapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoDetailViewModel
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoEditViewModel
import be.aidenstorme.todoapplication.ui.views.ToDoDetailView
import be.aidenstorme.todoapplication.ui.views.ToDoEditView
import be.aidenstorme.todoapplication.ui.views.ToDoListView

@Composable
fun ToDoNav(
    modifier: Modifier = Modifier
) {
    val navigator = remember { Navigator() }

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        modifier = modifier,
        // De ViewModelStore-decorator zorgt dat elke key (bv. elk detailscherm) zijn eigen ViewModel krijgt
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.ToDoList> {
                ToDoListView(
                    onToDoClick = { toDo -> navigator.navigate(Screen.ToDoDetail(toDo.number)) },
                    onAddToDoClick = { navigator.navigate(Screen.ToDoEdit(null)) }
                )
            }

            entry<Screen.ToDoDetail> { key ->
                val viewModel = hiltViewModel<ToDoDetailViewModel, ToDoDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(key.todoNumber) }
                )
                ToDoDetailView(
                    viewModel = viewModel,
                    onBack = { navigator.goBack() },
                    onEdit = { navigator.navigate(Screen.ToDoEdit(key.todoNumber)) }
                )
            }

            entry<Screen.ToDoEdit> { key ->
                val viewModel = hiltViewModel<ToDoEditViewModel, ToDoEditViewModel.Factory>(
                    creationCallback = { factory -> factory.create(key.todoNumber) }
                )
                ToDoEditView(
                    viewModel = viewModel,
                    onCancel = { navigator.goBack() },
                    onSave = { navigator.goBack() }
                )
            }
        }
    )
}

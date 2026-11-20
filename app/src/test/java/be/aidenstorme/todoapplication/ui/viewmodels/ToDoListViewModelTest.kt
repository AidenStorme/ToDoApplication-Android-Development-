package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.testutil.FakeToDoRepository
import be.aidenstorme.todoapplication.testutil.FakeUserRepository
import be.aidenstorme.todoapplication.testutil.MainDispatcherRule
import be.aidenstorme.todoapplication.testutil.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ToDoListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val toDoRepository = FakeToDoRepository(TestData.toDos)
    private val userRepository = FakeUserRepository(TestData.users)

    private fun createViewModel() = ToDoListViewModel(
        toDoRepository = toDoRepository,
        userRepository = userRepository
    )

    @Test
    fun uiState_containsToDosAndUsersFromRepositories() {
        val viewModel = createViewModel()

        assertEquals(TestData.toDos, viewModel.uiState.value.toDos)
        assertEquals(TestData.users, viewModel.uiState.value.users)
    }

    @Test
    fun uiState_updatesWhenToDoIsAdded() = runTest {
        val viewModel = createViewModel()

        toDoRepository.insertToDo(ToDo(title = "Nieuw", createdByUserId = 1))

        assertEquals(3, viewModel.uiState.value.toDos.size)
        assertTrue(viewModel.uiState.value.toDos.any { it.title == "Nieuw" })
    }

    @Test
    fun uiState_isEmptyWithoutData() {
        val viewModel = ToDoListViewModel(
            toDoRepository = FakeToDoRepository(),
            userRepository = FakeUserRepository()
        )

        assertTrue(viewModel.uiState.value.toDos.isEmpty())
    }
}

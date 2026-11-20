package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.testutil.FakeToDoRepository
import be.aidenstorme.todoapplication.testutil.FakeUserRepository
import be.aidenstorme.todoapplication.testutil.MainDispatcherRule
import be.aidenstorme.todoapplication.testutil.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ToDoDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val toDoRepository = FakeToDoRepository(TestData.toDos)

    private fun createViewModel(todoNumber: Int) = ToDoDetailViewModel(
        todoNumber = todoNumber,
        toDoRepository = toDoRepository,
        userRepository = FakeUserRepository(TestData.users)
    )

    private fun savedToDo(number: Int) = toDoRepository.currentToDos.first { it.number == number }

    @Test
    fun uiState_loadsToDoWithAssignedAndCreatorUser() {
        val viewModel = createViewModel(todoNumber = 2)

        val state = viewModel.uiState.value
        assertEquals(TestData.assignedToDo, state.toDo)
        assertEquals(TestData.jane, state.assignedUser)
        assertEquals(TestData.aiden, state.createdByUser)
    }

    @Test
    fun uiState_hasNoAssignedUserForUnassignedToDo() {
        val viewModel = createViewModel(todoNumber = 1)

        assertEquals(TestData.unassignedToDo, viewModel.uiState.value.toDo)
        assertNull(viewModel.uiState.value.assignedUser)
    }

    @Test
    fun uiState_hasNoToDoForUnknownNumber() {
        val viewModel = createViewModel(todoNumber = 99)

        assertNull(viewModel.uiState.value.toDo)
    }

    @Test
    fun onAnalysisDoneChange_savesChange() {
        val viewModel = createViewModel(todoNumber = 2)

        viewModel.onAnalysisDoneChange(true)

        assertTrue(savedToDo(2).analysisDone)
        assertTrue(viewModel.uiState.value.toDo!!.analysisDone)
    }

    @Test
    fun onDevelopmentDoneChange_savesChange() {
        val viewModel = createViewModel(todoNumber = 2)

        viewModel.onDevelopmentDoneChange(true)

        assertTrue(savedToDo(2).developmentDone)
    }

    @Test
    fun onReviewAndTestingDoneChange_savesChange() {
        val viewModel = createViewModel(todoNumber = 2)

        viewModel.onReviewAndTestingDoneChange(true)

        assertTrue(savedToDo(2).reviewAndTestingDone)
    }

    @Test
    fun onAcceptanceDoneChange_setsFinishedDateWhenToDoIsAssigned() {
        val viewModel = createViewModel(todoNumber = 2)

        viewModel.onAcceptanceDoneChange(true)

        assertTrue(savedToDo(2).acceptanceDone)
        assertNotNull(savedToDo(2).finishedOnDate)
    }

    @Test
    fun onAcceptanceDoneChange_doesNotSetFinishedDateWhenToDoIsUnassigned() {
        val viewModel = createViewModel(todoNumber = 1)

        viewModel.onAcceptanceDoneChange(true)

        assertTrue(savedToDo(1).acceptanceDone)
        assertNull(savedToDo(1).finishedOnDate)
    }

    @Test
    fun onAcceptanceDoneChange_false_clearsFinishedDate() {
        val viewModel = createViewModel(todoNumber = 2)
        viewModel.onAcceptanceDoneChange(true)

        viewModel.onAcceptanceDoneChange(false)

        assertFalse(savedToDo(2).acceptanceDone)
        assertNull(savedToDo(2).finishedOnDate)
    }

    @Test
    fun changes_areIgnoredWhenToDoDoesNotExist() {
        val viewModel = createViewModel(todoNumber = 99)

        viewModel.onAnalysisDoneChange(true)

        assertEquals(TestData.toDos, toDoRepository.currentToDos)
    }
}

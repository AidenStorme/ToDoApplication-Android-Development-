package be.aidenstorme.todoapplication.ui.viewmodels

import be.aidenstorme.todoapplication.testutil.FakeActivityRepository
import be.aidenstorme.todoapplication.testutil.FakeToDoRepository
import be.aidenstorme.todoapplication.testutil.FakeUserRepository
import be.aidenstorme.todoapplication.testutil.MainDispatcherRule
import be.aidenstorme.todoapplication.testutil.TestData
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ToDoEditViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val toDoRepository = FakeToDoRepository(TestData.toDos)
    private val activityRepository = FakeActivityRepository()

    private fun createViewModel(todoNumber: Int?) = ToDoEditViewModel(
        todoNumber = todoNumber,
        toDoRepository = toDoRepository,
        userRepository = FakeUserRepository(TestData.users),
        activityRepository = activityRepository
    )

    @Test
    fun newToDo_startsWithEmptyForm() {
        val state = createViewModel(todoNumber = null).uiState.value

        assertNull(state.toDoToEdit)
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertNull(state.assignedUser)
        assertEquals(TestData.users, state.users)
    }

    @Test
    fun existingToDo_fillsFormWithToDoValues() {
        val state = createViewModel(todoNumber = 2).uiState.value

        assertEquals(TestData.assignedToDo, state.toDoToEdit)
        assertEquals(TestData.assignedToDo.title, state.title)
        assertEquals(TestData.assignedToDo.description, state.description)
        assertEquals(TestData.jane, state.assignedUser)
    }

    @Test
    fun onChangeFunctions_updateUiState() {
        val viewModel = createViewModel(todoNumber = null)

        viewModel.onTitleChange("Titel")
        viewModel.onDescriptionChange("Beschrijving")
        viewModel.onAssignedUserChange(TestData.aiden)
        viewModel.onAnalysisDoneChange(true)
        viewModel.onDevelopmentDoneChange(true)
        viewModel.onReviewAndTestingDoneChange(true)
        viewModel.onAcceptanceDoneChange(true)

        val state = viewModel.uiState.value
        assertEquals("Titel", state.title)
        assertEquals("Beschrijving", state.description)
        assertEquals(TestData.aiden, state.assignedUser)
        assertTrue(state.analysisDone)
        assertTrue(state.developmentDone)
        assertTrue(state.reviewAndTestingDone)
        assertTrue(state.acceptanceDone)
    }

    @Test
    fun onSave_newToDo_isInsertedAndCallbackIsCalled() {
        val viewModel = createViewModel(todoNumber = null)
        viewModel.onTitleChange("Nieuwe taak")
        viewModel.onAssignedUserChange(TestData.jane)
        var saved = false

        viewModel.onSave { saved = true }

        val inserted = toDoRepository.currentToDos.last()
        assertTrue(saved)
        assertEquals(3, toDoRepository.currentToDos.size)
        assertEquals("Nieuwe taak", inserted.title)
        assertEquals(TestData.jane.id, inserted.assignedToUserId)
        // Zonder bestaande to-do wordt de eerste gebruiker de aanmaker
        assertEquals(TestData.aiden.id, inserted.createdByUserId)
    }

    @Test
    fun onSave_existingToDo_isUpdatedAndKeepsCreator() {
        val viewModel = createViewModel(todoNumber = 2)
        viewModel.onTitleChange("Aangepaste titel")
        var saved = false

        viewModel.onSave { saved = true }

        val updated = toDoRepository.currentToDos.first { it.number == 2 }
        assertTrue(saved)
        assertEquals(2, toDoRepository.currentToDos.size)
        assertEquals("Aangepaste titel", updated.title)
        assertEquals(TestData.assignedToDo.createdByUserId, updated.createdByUserId)
    }

    @Test
    fun fetchRandomActivity_success_fillsTitleAndDescription() {
        val viewModel = createViewModel(todoNumber = null)

        viewModel.fetchRandomActivity()

        val state = viewModel.uiState.value
        assertEquals("Learn Hilt", state.title)
        assertEquals("education", state.description)
        assertNull(state.activityError)
    }

    @Test
    fun fetchRandomActivity_ioException_setsNetworkError() {
        activityRepository.error = IOException("geen verbinding")
        val viewModel = createViewModel(todoNumber = null)

        viewModel.fetchRandomActivity()

        assertEquals(ActivityError.NETWORK, viewModel.uiState.value.activityError)
        assertEquals("", viewModel.uiState.value.title)
    }

    @Test
    fun fetchRandomActivity_httpException_setsServerError() {
        activityRepository.error = HttpException(Response.error<Any>(500, "".toResponseBody()))
        val viewModel = createViewModel(todoNumber = null)

        viewModel.fetchRandomActivity()

        assertEquals(ActivityError.SERVER, viewModel.uiState.value.activityError)
    }

    @Test
    fun fetchRandomActivity_success_clearsPreviousError() {
        activityRepository.error = IOException()
        val viewModel = createViewModel(todoNumber = null)
        viewModel.fetchRandomActivity()

        activityRepository.error = null
        viewModel.fetchRandomActivity()

        assertNull(viewModel.uiState.value.activityError)
    }
}

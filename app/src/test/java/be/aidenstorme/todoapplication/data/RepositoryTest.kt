package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.BoredActivityResponse
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.testutil.FakeToDoDao
import be.aidenstorme.todoapplication.testutil.FakeUserDao
import be.aidenstorme.todoapplication.testutil.TestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryTest {

    @Test
    fun toDoRepository_getAllToDos_returnsDaoContent() = runTest {
        val repository = OfflineToDoRepository(FakeToDoDao(TestData.toDos))

        assertEquals(TestData.toDos, repository.getAllToDos().first())
    }

    @Test
    fun toDoRepository_getToDoByNumber_returnsMatchingToDoOrNull() = runTest {
        val repository = OfflineToDoRepository(FakeToDoDao(TestData.toDos))

        assertEquals(TestData.assignedToDo, repository.getToDoByNumber(2).first())
        assertNull(repository.getToDoByNumber(99).first())
    }

    @Test
    fun toDoRepository_insertToDo_savesToDoAndReturnsNumber() = runTest {
        val dao = FakeToDoDao(TestData.toDos)
        val repository = OfflineToDoRepository(dao)

        val number = repository.insertToDo(ToDo(title = "Nieuw", createdByUserId = 1))

        assertEquals(3L, number)
        assertEquals("Nieuw", dao.currentToDos.last().title)
    }

    @Test
    fun toDoRepository_updateToDo_replacesExistingToDo() = runTest {
        val dao = FakeToDoDao(TestData.toDos)
        val repository = OfflineToDoRepository(dao)

        repository.updateToDo(TestData.assignedToDo.copy(title = "Aangepast"))

        assertEquals("Aangepast", dao.currentToDos.first { it.number == 2 }.title)
        assertEquals(2, dao.currentToDos.size)
    }

    @Test
    fun toDoRepository_deleteToDo_removesToDo() = runTest {
        val dao = FakeToDoDao(TestData.toDos)
        val repository = OfflineToDoRepository(dao)

        repository.deleteToDo(TestData.unassignedToDo)

        assertEquals(listOf(TestData.assignedToDo), dao.currentToDos)
    }

    @Test
    fun userRepository_getAllUsers_returnsDaoContent() = runTest {
        val repository = OfflineUserRepository(FakeUserDao(TestData.users))

        assertEquals(TestData.users, repository.getAllUsers().first())
    }

    @Test
    fun userRepository_getUserById_returnsMatchingUserOrNull() = runTest {
        val repository = OfflineUserRepository(FakeUserDao(TestData.users))

        assertEquals(TestData.jane, repository.getUserById(2).first())
        assertNull(repository.getUserById(99).first())
    }

    @Test
    fun userRepository_insertAndUpdateUser_changeDaoContent() = runTest {
        val dao = FakeUserDao(TestData.users)
        val repository = OfflineUserRepository(dao)

        val id = repository.insertUser(TestData.aiden.copy(id = 0, userName = "nieuw"))
        repository.updateUser(TestData.jane.copy(firstName = "Janet"))

        assertEquals(3L, id)
        assertEquals("Janet", dao.currentUsers.first { it.id == 2 }.firstName)
        assertEquals(3, dao.currentUsers.size)
    }

    @Test
    fun activityRepository_getRandomActivity_returnsApiResponse() = runTest {
        val expected = BoredActivityResponse(activity = "Go for a walk", type = "relaxation")
        val repository = NetworkActivityRepository(object : BoredApiService {
            override suspend fun getRandomActivity() = expected
        })

        assertEquals(expected, repository.getRandomActivity())
    }
}

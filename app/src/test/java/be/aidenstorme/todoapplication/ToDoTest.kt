package be.aidenstorme.todoapplication

import be.aidenstorme.todoapplication.models.Status
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class ToDoTest {

    @Test
    fun testToDoCreationWithAsteriskConstructor() {
        val user1 = User(id = 1, userName = "johndoe", firstName = "John", lastName = "Doe", password = "secretpassword", isActive = true)
        val user2 = User(id = 2, userName = "janesmith", firstName = "Jane", lastName = "Smith", password = "secretpassword2", isActive = true)
        val now = Date()

        val todo = ToDo(
            number = 101,
            title = "Implement Models",
            description = "Create User and ToDo model classes",
            createdByUser = user1,
            createOnDate = now,
            assignedToUser = user2,
            finishedOnDate = null,
            timeEstimated = 4
        )

        assertEquals(101, todo.number)
        assertEquals("Implement Models", todo.title)
        assertEquals("Create User and ToDo model classes", todo.description)
        assertEquals(user1, todo.createdByUser)
        assertEquals(now, todo.createOnDate)
        assertEquals(user2, todo.assignedToUser)
        assertNull(todo.finishedOnDate)
        assertEquals(4, todo.timeEstimated)
        assertFalse(todo.analysisDone)
        assertFalse(todo.developmentDone)
        assertFalse(todo.reviewAndTestingDone)
        assertFalse(todo.acceptanceDone)
    }

    @Test
    fun testStatusAndStatusDescription() {
        val user1 = User(id = 1, userName = "johndoe", firstName = "John", lastName = "Doe", password = "secretpassword", isActive = true)
        val user2 = User(id = 2, userName = "janesmith", firstName = "Jane", lastName = "Smith", password = "secretpassword2", isActive = true)
        val now = Date()

        val todo = ToDo(
            number = 1,
            title = "Test",
            description = "Desc",
            createdByUser = user1,
            createOnDate = now,
            assignedToUser = null,
            finishedOnDate = null,
            timeEstimated = 10
        )

        assertEquals(Status.NEW, todo.status)
        assertEquals(R.string.status_new, todo.statusDescription)

        todo.assignedToUser = user2
        assertEquals(Status.ASSIGNED, todo.status)
        assertEquals(R.string.status_assigned, todo.statusDescription)

        todo.finishedOnDate = Date()
        assertEquals(Status.FINISHED, todo.status)
        assertEquals(R.string.status_finished, todo.statusDescription)
    }

    @Test
    fun testTimeRemaining() {
        val user = User(id = 1, userName = "johndoe", firstName = "John", lastName = "Doe", password = "secretpassword", isActive = true)
        val todo = ToDo(
            number = 1,
            title = "Test",
            description = "Desc",
            createdByUser = user,
            createOnDate = Date(),
            assignedToUser = null,
            finishedOnDate = null,
            timeEstimated = 100
        )

        assertEquals(100, todo.timeRemaining)

        todo.analysisDone = true
        assertEquals(85, todo.timeRemaining)

        todo.developmentDone = true
        assertEquals(30, todo.timeRemaining)

        todo.reviewAndTestingDone = true
        assertEquals(10, todo.timeRemaining)

        todo.acceptanceDone = true
        assertEquals(0, todo.timeRemaining)
    }
}

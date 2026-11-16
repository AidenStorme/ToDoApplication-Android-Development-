package be.aidenstorme.todoapplication

import be.aidenstorme.todoapplication.models.Status
import be.aidenstorme.todoapplication.models.ToDo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class ToDoTest {

    @Test
    fun testToDoCreationWithAsteriskConstructor() {
        val now = System.currentTimeMillis()

        val todo = ToDo(
            number = 101,
            title = "Implement Models",
            description = "Create User and ToDo model classes",
            createdByUserId = 1,
            createOnDate = now,
            assignedToUserId = 2,
            finishedOnDate = null,
            timeEstimated = 4
        )

        assertEquals(101, todo.number)
        assertEquals("Implement Models", todo.title)
        assertEquals("Create User and ToDo model classes", todo.description)
        assertEquals(1, todo.createdByUserId)
        assertEquals(now, todo.createOnDate)
        assertEquals(2, todo.assignedToUserId)
        assertNull(todo.finishedOnDate)
        assertEquals(4, todo.timeEstimated)
        assertFalse(todo.analysisDone)
        assertFalse(todo.developmentDone)
        assertFalse(todo.reviewAndTestingDone)
        assertFalse(todo.acceptanceDone)
    }

    @Test
    fun testStatusAndStatusDescription() {
        val now = System.currentTimeMillis()

        val todo = ToDo(
            number = 1,
            title = "Test",
            description = "Desc",
            createdByUserId = 1,
            createOnDate = now,
            assignedToUserId = null,
            finishedOnDate = null,
            timeEstimated = 10
        )

        assertEquals(Status.NEW, todo.status)
        assertEquals(R.string.status_new, todo.statusDescription)

        todo.assignedToUserId = 2
        assertEquals(Status.ASSIGNED, todo.status)
        assertEquals(R.string.status_assigned, todo.statusDescription)

        todo.finishedOnDate = System.currentTimeMillis()
        assertEquals(Status.FINISHED, todo.status)
        assertEquals(R.string.status_finished, todo.statusDescription)
    }

    @Test
    fun testTimeRemaining() {
        val todo = ToDo(
            number = 1,
            title = "Test",
            description = "Desc",
            createdByUserId = 1,
            createOnDate = System.currentTimeMillis(),
            assignedToUserId = null,
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

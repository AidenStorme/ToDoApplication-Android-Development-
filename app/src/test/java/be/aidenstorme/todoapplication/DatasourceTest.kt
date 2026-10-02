package be.aidenstorme.todoapplication

import be.aidenstorme.todoapplication.data.Datasource
import be.aidenstorme.todoapplication.models.Status
import org.junit.Assert.assertEquals
import org.junit.Test

class DatasourceTest {

    @Test
    fun testGetUsersReturnsThreeUsers() {
        val users = Datasource.getUsers()
        assertEquals(3, users.size)
    }

    @Test
    fun testGetToDosReturnsFiveToDosWithDifferentStatuses() {
        val todos = Datasource.getToDos()
        assertEquals(5, todos.size)

        val newCount = todos.count { it.status == Status.NEW }
        val assignedCount = todos.count { it.status == Status.ASSIGNED }
        val finishedCount = todos.count { it.status == Status.FINISHED }

        assertEquals(1, newCount)
        assertEquals(2, assignedCount)
        assertEquals(2, finishedCount)
    }
}

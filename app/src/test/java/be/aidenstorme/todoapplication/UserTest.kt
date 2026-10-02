package be.aidenstorme.todoapplication

import be.aidenstorme.todoapplication.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserTest {

    @Test
    fun testEmptyConstructor() {
        val user = User()
        assertEquals(0, user.id)
        assertEquals("", user.userName)
        assertEquals("", user.firstName)
        assertEquals("", user.lastName)
        assertEquals("", user.password)
        assertFalse(user.isActive)
    }

    @Test
    fun testAllFieldsConstructor() {
        val user = User(
            id = 1,
            userName = "johndoe",
            firstName = "John",
            lastName = "Doe",
            password = "securePassword123",
            isActive = true
        )
        assertEquals(1, user.id)
        assertEquals("johndoe", user.userName)
        assertEquals("John", user.firstName)
        assertEquals("Doe", user.lastName)
        assertEquals("securePassword123", user.password)
        assertTrue(user.isActive)
    }
}

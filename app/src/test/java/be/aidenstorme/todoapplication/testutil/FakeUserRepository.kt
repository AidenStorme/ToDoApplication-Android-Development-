package be.aidenstorme.todoapplication.testutil

import be.aidenstorme.todoapplication.data.UserRepository
import be.aidenstorme.todoapplication.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory UserRepository voor ViewModel-tests (geen database nodig). */
class FakeUserRepository(initialUsers: List<User> = emptyList()) : UserRepository {

    private val users = MutableStateFlow(initialUsers)

    override fun getAllUsers(): Flow<List<User>> = users

    override fun getUserById(id: Int): Flow<User?> =
        users.map { list -> list.find { it.id == id } }

    override suspend fun insertUser(user: User): Long {
        val id = if (user.id == 0) (users.value.maxOfOrNull { it.id } ?: 0) + 1 else user.id
        users.update { it + user.copy(id = id) }
        return id.toLong()
    }

    override suspend fun updateUser(user: User) {
        users.update { list -> list.map { if (it.id == user.id) user else it } }
    }
}

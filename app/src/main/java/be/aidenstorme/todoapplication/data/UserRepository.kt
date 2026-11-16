package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
    fun getUserById(id: Int): Flow<User?>
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)
}

class OfflineUserRepository(private val userDao: UserDao) : UserRepository {
    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    override fun getUserById(id: Int): Flow<User?> = userDao.getUserById(id)
    override suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    override suspend fun updateUser(user: User) = userDao.updateUser(user)
}

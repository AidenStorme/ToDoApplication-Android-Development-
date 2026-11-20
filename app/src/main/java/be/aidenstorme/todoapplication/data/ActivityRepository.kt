package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.BoredActivityResponse
import javax.inject.Inject

interface ActivityRepository {
    suspend fun getRandomActivity(): BoredActivityResponse
}

class NetworkActivityRepository @Inject constructor(
    private val boredApiService: BoredApiService
) : ActivityRepository {
    override suspend fun getRandomActivity(): BoredActivityResponse =
        boredApiService.getRandomActivity()
}

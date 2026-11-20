package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.BoredActivityResponse

interface ActivityRepository {
    suspend fun getRandomActivity(): BoredActivityResponse
}

class NetworkActivityRepository(private val boredApiService: BoredApiService) : ActivityRepository {
    override suspend fun getRandomActivity(): BoredActivityResponse =
        boredApiService.getRandomActivity()
}

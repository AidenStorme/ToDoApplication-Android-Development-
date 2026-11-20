package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.BoredActivityResponse
import retrofit2.http.GET

interface BoredApiService {
    @GET("random")
    suspend fun getRandomActivity(): BoredActivityResponse
}

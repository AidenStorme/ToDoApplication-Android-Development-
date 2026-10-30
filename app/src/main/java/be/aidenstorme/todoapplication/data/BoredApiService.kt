package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.BoredActivityResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface BoredApiService {
    @GET("random")
    suspend fun getRandomActivity(): BoredActivityResponse

    companion object {
        private const val BASE_URL = "https://bored-api.appbrewery.com/"

        val retrofitService: BoredApiService by lazy {
            val contentType = "application/json".toMediaType()
            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
                .create(BoredApiService::class.java)
        }
    }
}

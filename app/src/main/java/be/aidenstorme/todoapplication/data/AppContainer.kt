package be.aidenstorme.todoapplication.data

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val userRepository: UserRepository
    val toDoRepository: ToDoRepository
    val activityRepository: ActivityRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "https://bored-api.appbrewery.com/"

    private val retrofitService: BoredApiService by lazy {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BoredApiService::class.java)
    }

    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(ToDoDatabase.getDatabase(context).userDao())
    }

    override val toDoRepository: ToDoRepository by lazy {
        OfflineToDoRepository(ToDoDatabase.getDatabase(context).toDoDao())
    }

    override val activityRepository: ActivityRepository by lazy {
        NetworkActivityRepository(retrofitService)
    }
}

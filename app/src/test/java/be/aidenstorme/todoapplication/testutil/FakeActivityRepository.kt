package be.aidenstorme.todoapplication.testutil

import be.aidenstorme.todoapplication.data.ActivityRepository
import be.aidenstorme.todoapplication.models.BoredActivityResponse

/** Geeft een vaste activiteit terug, of gooit de ingestelde fout. */
class FakeActivityRepository : ActivityRepository {

    var response = BoredActivityResponse(activity = "Learn Hilt", type = "education")
    var error: Exception? = null

    override suspend fun getRandomActivity(): BoredActivityResponse {
        error?.let { throw it }
        return response
    }
}

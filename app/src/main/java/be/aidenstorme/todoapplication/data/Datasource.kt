package be.aidenstorme.todoapplication.data

import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User
import java.util.Date

object Datasource {

    fun getUsers(): List<User> {
        return listOf(
            User(
                id = 1,
                userName = "AidenStorme",
                firstName = "Aiden",
                lastName = "Storme",
                password = "Password123",
                isActive = true
            ),
            User(
                id = 2,
                userName = "janesmith",
                firstName = "Jane",
                lastName = "Smith",
                password = "Password456",
                isActive = true
            ),
            User(
                id = 3,
                userName = "peterparker",
                firstName = "Peter",
                lastName = "Parker",
                password = "Password789",
                isActive = false
            )
        )
    }

    fun getToDos(): List<ToDo> {
        val users = getUsers()
        val userDirk = users[0]
        val userJane = users[1]
        val userPeter = users[2]

        val now = Date()
        val yesterday = Date(now.time - (24 * 60 * 60 * 1000L))

        return listOf(
            // 1. Exact item from the assignment PDF screenshot
            ToDo(
                number = 1,
                title = "Finish detail ToDo",
                description = "Add extra fields like assigned user, time estimated, ...to the ToDo detail screen",
                createdByUser = userDirk,
                createOnDate = yesterday,
                assignedToUser = userDirk,
                finishedOnDate = now,
                timeEstimated = 20,
                analysisDone = false,
                developmentDone = false,
                reviewAndTestingDone = false,
                acceptanceDone = false
            ),
            // 2. Status: NEW (Unassigned)
            ToDo(
                number = 2,
                title = "Database schema ontwerpen",
                description = "Ontwerp het ERD en de entiteiten voor de lokale Room database.",
                createdByUser = userDirk,
                createOnDate = yesterday,
                assignedToUser = null,
                finishedOnDate = null,
                timeEstimated = 8,
                analysisDone = false,
                developmentDone = false,
                reviewAndTestingDone = false,
                acceptanceDone = false
            ),
            // 3. Status: ASSIGNED (Assigned to Jane, in development)
            ToDo(
                number = 3,
                title = "User Interface bouwen",
                description = "Implementeer het Jetpack Compose scherm met een LazyColumn voor ToDo items.",
                createdByUser = userDirk,
                createOnDate = yesterday,
                assignedToUser = userJane,
                finishedOnDate = null,
                timeEstimated = 12,
                analysisDone = true,
                developmentDone = true,
                reviewAndTestingDone = false,
                acceptanceDone = false
            ),
            // 4. Status: ASSIGNED (Assigned to Dirk, in review & testing)
            ToDo(
                number = 4,
                title = "Unit tests schrijven",
                description = "Schrijf unit tests voor het ToDo model en de status berekeningen.",
                createdByUser = userPeter,
                createOnDate = yesterday,
                assignedToUser = userDirk,
                finishedOnDate = null,
                timeEstimated = 6,
                analysisDone = true,
                developmentDone = true,
                reviewAndTestingDone = true,
                acceptanceDone = false
            ),
            // 5. Status: FINISHED (Assigned to Peter, completed)
            ToDo(
                number = 5,
                title = "Projectopzet maken",
                description = "Initialiseer het Android Studio project en voeg de basismodellen toe.",
                createdByUser = userDirk,
                createOnDate = yesterday,
                assignedToUser = userPeter,
                finishedOnDate = now,
                timeEstimated = 2,
                analysisDone = true,
                developmentDone = true,
                reviewAndTestingDone = true,
                acceptanceDone = true
            )
        )
    }
}

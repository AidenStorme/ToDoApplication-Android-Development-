package be.aidenstorme.todoapplication.testutil

import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User

object TestData {

    val aiden = User(id = 1, userName = "aiden", firstName = "Aiden", lastName = "Storme", isActive = true)
    val jane = User(id = 2, userName = "jane", firstName = "Jane", lastName = "Smith", isActive = true)

    val users = listOf(aiden, jane)

    /** Nog niet toegewezen to-do. */
    val unassignedToDo = ToDo(
        number = 1,
        title = "Database ontwerpen",
        description = "ERD maken",
        createdByUserId = aiden.id,
        timeEstimated = 8
    )

    /** To-do toegewezen aan Jane, aangemaakt door Aiden. */
    val assignedToDo = ToDo(
        number = 2,
        title = "UI bouwen",
        description = "LazyColumn maken",
        createdByUserId = aiden.id,
        assignedToUserId = jane.id,
        timeEstimated = 12
    )

    val toDos = listOf(unassignedToDo, assignedToDo)
}

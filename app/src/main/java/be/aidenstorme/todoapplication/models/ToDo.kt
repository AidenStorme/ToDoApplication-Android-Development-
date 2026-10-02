package be.aidenstorme.todoapplication.models

import androidx.annotation.StringRes
import be.aidenstorme.todoapplication.R
import java.util.Date
import kotlin.math.roundToInt

data class ToDo(
    var number: Int,
    var title: String,
    var description: String,
    var createdByUser: User,
    var createOnDate: Date = Date(),
    var assignedToUser: User? = null,
    var finishedOnDate: Date? = null,
    var timeEstimated: Int = 0,
    var analysisDone: Boolean = false,
    var developmentDone: Boolean = false,
    var reviewAndTestingDone: Boolean = false,
    var acceptanceDone: Boolean = false
) {
    // Secondary constructor for the 8 fields marked with * in specification
    constructor(
        number: Int,
        title: String,
        description: String,
        createdByUser: User,
        createOnDate: Date,
        assignedToUser: User?,
        finishedOnDate: Date?,
        timeEstimated: Int
    ) : this(
        number = number,
        title = title,
        description = description,
        createdByUser = createdByUser,
        createOnDate = createOnDate,
        assignedToUser = assignedToUser,
        finishedOnDate = finishedOnDate,
        timeEstimated = timeEstimated,
        analysisDone = false,
        developmentDone = false,
        reviewAndTestingDone = false,
        acceptanceDone = false
    )

    val status: Status
        get() {
            return if (assignedToUser != null && finishedOnDate != null) {
                Status.FINISHED
            } else if (assignedToUser != null) {
                Status.ASSIGNED
            } else {
                Status.NEW
            }
        }

    @get:StringRes
    val statusDescription: Int
        get() = when (status) {
            Status.NEW -> R.string.status_new
            Status.ASSIGNED -> R.string.status_assigned
            Status.FINISHED -> R.string.status_finished
        }

    val timeRemaining: Int
        get() = when {
            acceptanceDone -> 0
            reviewAndTestingDone -> (timeEstimated * 0.10).roundToInt()
            developmentDone -> (timeEstimated * 0.30).roundToInt()
            analysisDone -> (timeEstimated * 0.85).roundToInt()
            else -> timeEstimated
        }
}

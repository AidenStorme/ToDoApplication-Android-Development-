package be.aidenstorme.todoapplication.models

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import be.aidenstorme.todoapplication.R
import kotlin.math.roundToInt

@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["assignedToUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["createdByUserId"]),
        Index(value = ["assignedToUserId"])
    ]
)
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    var number: Int = 0,
    var title: String = "",
    var description: String = "",
    var createdByUserId: Int = 0,
    var createOnDate: Long = System.currentTimeMillis(),
    var assignedToUserId: Int? = null,
    var finishedOnDate: Long? = null,
    var timeEstimated: Int = 0,
    var analysisDone: Boolean = false,
    var developmentDone: Boolean = false,
    var reviewAndTestingDone: Boolean = false,
    var acceptanceDone: Boolean = false
) {
    @Ignore
    constructor(
        number: Int,
        title: String,
        description: String,
        createdByUserId: Int,
        createOnDate: Long,
        assignedToUserId: Int?,
        finishedOnDate: Long?,
        timeEstimated: Int
    ) : this(
        number = number,
        title = title,
        description = description,
        createdByUserId = createdByUserId,
        createOnDate = createOnDate,
        assignedToUserId = assignedToUserId,
        finishedOnDate = finishedOnDate,
        timeEstimated = timeEstimated,
        analysisDone = false,
        developmentDone = false,
        reviewAndTestingDone = false,
        acceptanceDone = false
    )

    val status: Status
        get() {
            return if (assignedToUserId != null && finishedOnDate != null) {
                Status.FINISHED
            } else if (assignedToUserId != null) {
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

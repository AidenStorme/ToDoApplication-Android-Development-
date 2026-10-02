package be.aidenstorme.todoapplication.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.aidenstorme.todoapplication.R
import be.aidenstorme.todoapplication.models.ToDo
import java.util.Date
import java.util.Locale

@Composable
fun ToDoDetailView(toDo: ToDo, modifier: Modifier = Modifier) {
    // Mutable state for the 4 boolean switches
    var analysisDone by remember(toDo) { mutableStateOf(toDo.analysisDone) }
    var developmentDone by remember(toDo) { mutableStateOf(toDo.developmentDone) }
    var reviewAndTestingDone by remember(toDo) { mutableStateOf(toDo.reviewAndTestingDone) }
    var acceptanceDone by remember(toDo) { mutableStateOf(toDo.acceptanceDone) }

    // Dynamic calculated states for status and remaining time
    var statusRes by remember(toDo) { mutableIntStateOf(toDo.statusDescription) }
    var timeRemaining by remember(toDo) { mutableIntStateOf(toDo.timeRemaining) }

    fun updateToDoState() {
        toDo.analysisDone = analysisDone
        toDo.developmentDone = developmentDone
        toDo.reviewAndTestingDone = reviewAndTestingDone
        toDo.acceptanceDone = acceptanceDone

        // Automatically update finishedOnDate and status
        if (acceptanceDone && toDo.assignedToUser != null) {
            toDo.finishedOnDate = toDo.finishedOnDate ?: Date()
        } else if (!acceptanceDone) {
            toDo.finishedOnDate = null
        }

        statusRes = toDo.statusDescription
        timeRemaining = toDo.timeRemaining
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Row: Logo on left, #Number and Status on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToDoLogoView(modifier = Modifier.size(72.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.todo_number, toDo.number),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                )
                Text(
                    text = stringResource(statusRes),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontStyle = FontStyle.Italic,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Red horizontal divider line
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Red,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Title
        Text(
            text = toDo.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )

        // Description
        Text(
            text = toDo.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Assigned To User
        val assignedText = if (toDo.assignedToUser != null) {
            stringResource(
                R.string.assigned_to,
                toDo.assignedToUser!!.firstName,
                toDo.assignedToUser!!.lastName
            )
        } else {
            stringResource(R.string.not_assigned)
        }
        Text(
            text = assignedText,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
        )

        // Time Estimated
        Text(
            text = stringResource(R.string.time_estimated, toDo.timeEstimated),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
        )

        // Time Remaining
        Text(
            text = stringResource(
                R.string.time_remaining,
                String.format(Locale.US, "%.2f", timeRemaining.toDouble())
            ),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Outlined Box containing the 4 workflow switches
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DetailSwitchRowView(
                    label = stringResource(R.string.label_analysis_done_q),
                    checked = analysisDone,
                    onCheckedChange = {
                        analysisDone = it
                        updateToDoState()
                    }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_development_done_q),
                    checked = developmentDone,
                    onCheckedChange = {
                        developmentDone = it
                        updateToDoState()
                    }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_review_testing_done_q),
                    checked = reviewAndTestingDone,
                    onCheckedChange = {
                        reviewAndTestingDone = it
                        updateToDoState()
                    }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_acceptance_done_q),
                    checked = acceptanceDone,
                    onCheckedChange = {
                        acceptanceDone = it
                        updateToDoState()
                    }
                )
            }
        }
    }
}

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.aidenstorme.todoapplication.R
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoDetailViewModel
import java.util.Locale

@Composable
fun ToDoDetailView(
    modifier: Modifier = Modifier,
    toDo: ToDo? = null,
    viewModel: ToDoDetailViewModel = viewModel()
) {
    LaunchedEffect(toDo) {
        if (toDo != null) {
            viewModel.setToDo(toDo)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val currentToDo = uiState.toDo ?: toDo ?: return

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
                    text = stringResource(R.string.todo_number, currentToDo.number),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                )
                Text(
                    text = stringResource(currentToDo.statusDescription),
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
            text = currentToDo.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )

        // Description
        Text(
            text = currentToDo.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Assigned To User
        val assignedText = if (currentToDo.assignedToUser != null) {
            stringResource(
                R.string.assigned_to,
                currentToDo.assignedToUser!!.firstName,
                currentToDo.assignedToUser!!.lastName
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
            text = stringResource(R.string.time_estimated, currentToDo.timeEstimated),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
        )

        // Time Remaining
        Text(
            text = stringResource(
                R.string.time_remaining,
                String.format(Locale.US, "%.2f", currentToDo.timeRemaining.toDouble())
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
                    checked = currentToDo.analysisDone,
                    onCheckedChange = { viewModel.onAnalysisDoneChange(it) }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_development_done_q),
                    checked = currentToDo.developmentDone,
                    onCheckedChange = { viewModel.onDevelopmentDoneChange(it) }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_review_testing_done_q),
                    checked = currentToDo.reviewAndTestingDone,
                    onCheckedChange = { viewModel.onReviewAndTestingDoneChange(it) }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_acceptance_done_q),
                    checked = currentToDo.acceptanceDone,
                    onCheckedChange = { viewModel.onAcceptanceDoneChange(it) }
                )
            }
        }
    }
}

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.aidenstorme.todoapplication.R
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.models.User
import be.aidenstorme.todoapplication.ui.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoEditView(
    modifier: Modifier = Modifier,
    toDoToEdit: ToDo? = null,
    users: List<User> = emptyList(),
    onSave: (ToDo) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    // Stateful / Observable UI variables
    var title by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.title ?: "") }
    var description by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.description ?: "") }
    var assignedUser by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.assignedToUser) }

    var analysisDone by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.analysisDone ?: false) }
    var developmentDone by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.developmentDone ?: false) }
    var reviewAndTestingDone by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.reviewAndTestingDone ?: false) }
    var acceptanceDone by remember(toDoToEdit) { mutableStateOf(toDoToEdit?.acceptanceDone ?: false) }

    var userDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top-Left: Logo image with text "Add/Edit ToDo" underneath it
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            ToDoLogoView(modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.title_add_edit_todo),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }

        // Red horizontal divider line
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Red,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // 1. Title AppTextField
        AppTextField(
            value = title,
            label = stringResource(R.string.label_title),
            singleLine = true
        ) { title = it }

        // 2. Description AppTextField
        AppTextField(
            value = description,
            label = stringResource(R.string.label_description),
            singleLine = false,
            minLines = 3,
            maxLines = 5
        ) { description = it }

        // 3. Assigned User Dropdown AppTextField
        ExposedDropdownMenuBox(
            expanded = userDropdownExpanded,
            onExpandedChange = { userDropdownExpanded = !userDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            AppTextField(
                value = assignedUser?.let {
                    stringResource(R.string.user_full_name, it.firstName, it.lastName)
                } ?: stringResource(R.string.not_assigned),
                label = stringResource(R.string.label_assigned_user),
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userDropdownExpanded) },
                modifier = Modifier.menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
            ) {}
            ExposedDropdownMenu(
                expanded = userDropdownExpanded,
                onDismissRequest = { userDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.not_assigned)) },
                    onClick = {
                        assignedUser = null
                        userDropdownExpanded = false
                    }
                )
                users.forEach { user ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    R.string.user_full_name,
                                    user.firstName,
                                    user.lastName
                                )
                            )
                        },
                        onClick = {
                            assignedUser = user
                            userDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Outlined Box containing the 4 workflow switches (Same layout as ToDoDetailView)
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
                    onCheckedChange = { analysisDone = it }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_development_done_q),
                    checked = developmentDone,
                    onCheckedChange = { developmentDone = it }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_review_testing_done_q),
                    checked = reviewAndTestingDone,
                    onCheckedChange = { reviewAndTestingDone = it }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_acceptance_done_q),
                    checked = acceptanceDone,
                    onCheckedChange = { acceptanceDone = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons: Cancel and Save
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.btn_cancel))
            }
            Button(
                onClick = {
                    val updatedToDo = toDoToEdit?.copy(
                        title = title,
                        description = description,
                        assignedToUser = assignedUser,
                        analysisDone = analysisDone,
                        developmentDone = developmentDone,
                        reviewAndTestingDone = reviewAndTestingDone,
                        acceptanceDone = acceptanceDone
                    ) ?: ToDo(
                        number = 0,
                        title = title,
                        description = description,
                        createdByUser = users.firstOrNull() ?: User(),
                        assignedToUser = assignedUser,
                        analysisDone = analysisDone,
                        developmentDone = developmentDone,
                        reviewAndTestingDone = reviewAndTestingDone,
                        acceptanceDone = acceptanceDone
                    )
                    onSave(updatedToDo)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}

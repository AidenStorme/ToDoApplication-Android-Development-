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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.aidenstorme.todoapplication.R
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.ui.AppTextField
import be.aidenstorme.todoapplication.ui.viewmodels.ToDoEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoEditView(
    modifier: Modifier = Modifier,
    toDoToEdit: ToDo? = null,
    viewModel: ToDoEditViewModel = viewModel(),
    onSave: (ToDo) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    LaunchedEffect(toDoToEdit) {
        viewModel.setToDoToEdit(toDoToEdit)
    }

    val uiState by viewModel.uiState.collectAsState()

    // UI-specific state for dropdown expanded (rememberSaveable, NOT in ViewModel)
    var userDropdownExpanded by rememberSaveable { mutableStateOf(false) }

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
            value = uiState.title,
            label = stringResource(R.string.label_title),
            singleLine = true
        ) { viewModel.onTitleChange(it) }

        // 2. Description AppTextField
        AppTextField(
            value = uiState.description,
            label = stringResource(R.string.label_description),
            singleLine = false,
            minLines = 3,
            maxLines = 5
        ) { viewModel.onDescriptionChange(it) }

        // 3. Assigned User Dropdown AppTextField
        ExposedDropdownMenuBox(
            expanded = userDropdownExpanded,
            onExpandedChange = { userDropdownExpanded = !userDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            AppTextField(
                value = uiState.assignedUser?.let {
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
                        viewModel.onAssignedUserChange(null)
                        userDropdownExpanded = false
                    }
                )
                uiState.users.forEach { user ->
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
                            viewModel.onAssignedUserChange(user)
                            userDropdownExpanded = false
                        }
                    )
                }
            }
        }

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
                    checked = uiState.analysisDone,
                    onCheckedChange = { viewModel.onAnalysisDoneChange(it) }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_development_done_q),
                    checked = uiState.developmentDone,
                    onCheckedChange = { viewModel.onDevelopmentDoneChange(it) }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_review_testing_done_q),
                    checked = uiState.reviewAndTestingDone,
                    onCheckedChange = { viewModel.onReviewAndTestingDoneChange(it) }
                )

                DetailSwitchRowView(
                    label = stringResource(R.string.label_acceptance_done_q),
                    checked = uiState.acceptanceDone,
                    onCheckedChange = { viewModel.onAcceptanceDoneChange(it) }
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
                    viewModel.onSave(onSave)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}

package be.aidenstorme.todoapplication.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.aidenstorme.todoapplication.R
import be.aidenstorme.todoapplication.models.ToDo
import be.aidenstorme.todoapplication.ui.AppToDoCard

@Composable
fun ToDoListView(
    toDos: List<ToDo>,
    modifier: Modifier = Modifier,
    onToDoClick: (ToDo) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Row: Logo on left, Screen Title on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToDoLogoView(modifier = Modifier.size(72.dp))
            Text(
                text = stringResource(R.string.title_todo_list),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }

        // Red horizontal divider line consistent with app styling
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Red,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Scrollable LazyColumn displaying all ToDo items
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(toDos, key = { it.number }) { toDo ->
                AppToDoCard(
                    toDo = toDo,
                    onClick = { onToDoClick(toDo) }
                )
            }
        }
    }
}

@Composable
fun ToDoListScreen(
    toDos: List<ToDo>,
    modifier: Modifier = Modifier,
    onToDoClick: (ToDo) -> Unit = {}
) {
    ToDoListView(
        toDos = toDos,
        modifier = modifier,
        onToDoClick = onToDoClick
    )
}

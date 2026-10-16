package be.aidenstorme.todoapplication.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import be.aidenstorme.todoapplication.data.Datasource
import be.aidenstorme.todoapplication.ui.views.AddEditToDoScreen

@Composable
fun ToDoApp(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        AddEditToDoScreen(
            users = Datasource.getUsers(),
            modifier = Modifier.padding(innerPadding)
        )
    }
}

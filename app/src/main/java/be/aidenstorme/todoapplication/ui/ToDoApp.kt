package be.aidenstorme.todoapplication.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import be.aidenstorme.todoapplication.ui.navigation.ToDoNav

@Composable
fun ToDoApp(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        ToDoNav(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

package be.aidenstorme.todoapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import be.aidenstorme.todoapplication.data.Datasource
import be.aidenstorme.todoapplication.ui.theme.ToDoApplicationTheme
import be.aidenstorme.todoapplication.ui.views.ToDoEditView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ToDoEditView(
                        modifier = Modifier.padding(innerPadding),
                        toDoToEdit = Datasource.getToDos()[0],
                        users = Datasource.getUsers()
                    )
                }
            }
        }
    }
}

package be.aidenstorme.todoapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import be.aidenstorme.todoapplication.ui.ToDoApp
import be.aidenstorme.todoapplication.ui.theme.ToDoApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoApplicationTheme {
                ToDoApp()
            }
        }
    }
}


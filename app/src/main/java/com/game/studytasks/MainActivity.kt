package com.game.studytasks

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.game.studytasks.ui.theme.StudyTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyTasksScreen(
                        name = "Study Tasks",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
data class StudyTask(
    val id: Int,
    val title: String,
    val category: String,
    val isDone: Boolean
)

@Composable
fun StudyTasksScreen(name: String, modifier: Modifier = Modifier) {


    val tasks = listOf(
        StudyTask(1, "Review data classes", "Kotlin", false),
        StudyTask(2, "Build a list with LazyColumn", "Jetpack Compose", false),
        StudyTask(3, "Understand remember and mutableStateOf", "State and Recomposition", false),
        StudyTask(4, "Move logic into a ViewModel", "Android Architecture", false),
        StudyTask(5, "Save tasks with Room", "Local Persistence", false)
    )
    Spacer(modifier = Modifier.height(25.dp))
    Column( Modifier.padding(35.dp)) {
        Text(text = "Study Tasks")

        Spacer(modifier = Modifier.height(20.dp))


            for (task in tasks) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(17.dp)
                    ) {
                        Text(text = "Task ${task.id}")
                        Text(text = task.title)
                        Text(text = "Language: ${task.category}")

                        if (task.isDone) {
                            Text(text = "Task Completed")
                        }

                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StudyTasksTheme {
        StudyTasksScreen("Android")
    }
}
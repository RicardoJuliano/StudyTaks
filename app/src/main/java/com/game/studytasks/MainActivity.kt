package com.game.studytasks

import android.os.Bundle
import android.service.autofill.OnClickAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.dp
import com.game.studytasks.ui.TaskViewModel
import com.game.studytasks.ui.theme.StudyTasksTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyTasksScreen(
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
fun StudyTasksScreen(modifier: Modifier = Modifier) {

    var selectedScreen by remember { mutableStateOf("Tasks") }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedScreen == "Tasks",
                    onClick = { selectedScreen = "Tasks" },
                    icon = { Text(text = "T") },
                    label = { Text(text = "Tasks") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Progress",
                    onClick = { selectedScreen = "Progress" },
                    icon = { Text(text = "P") },
                    label = { Text(text = "Progress") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Profile",
                    onClick = { selectedScreen = "Profile" },
                    icon = { Text(text = "U") },
                    label = { Text(text = "Profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (selectedScreen) {
                "Tasks" -> TasksContent()
                "Progress" -> ProgressContent()
                "Profile" -> ProfileContent()
            }
        }
    }

}
@Composable
fun TasksContent() {

    val viewModel: TaskViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }



    LazyColumn(
        modifier = Modifier.padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        item{  Text(text = "Study Tasks")}

        items(tasks){ task ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                     modifier = Modifier.padding(17.dp)
                ) {
                    Checkbox(
                        checked = task.isDone,
                        onCheckedChange = {viewModel.toggleTask(task)  }
                    )

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
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
        item{
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoria") })
            Button(onClick = {viewModel.createTask(title, category)
                title = ""
                category = ""
            }) {
                Text("Adcionar")
            }
        }

    }


}
@Composable
fun ProfileContent(){
    Text(text = "Profile")
}

@Composable
fun ProgressContent(){
    Text(text = "% Progress")
}


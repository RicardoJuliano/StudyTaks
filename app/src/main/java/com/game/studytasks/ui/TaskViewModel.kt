package com.game.studytasks.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.game.studytasks.StudyTasksApplication
import com.game.studytasks.data.TaskRepository
import com.game.studytasks.shared.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = getApplication<StudyTasksApplication>().database.taskDao()
    private val repository = TaskRepository(dao)


    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            repository.observeTask().collect { _tasks.value = it }
        }
        viewModelScope.launch {
            repository.refreshTask()
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val taskAtualizada = task.copy(isDone = !task.isDone)
            repository.updateTask(task.id, taskAtualizada)
                .onSuccess {}
        }
    }
    fun createTask(title: String, category: String) {
        viewModelScope.launch {
            val newTask = Task(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                category = category,
                isDone = false
            )
            repository.createTask(newTask)
                .onSuccess {}
        }
    }
    fun deleteTask(id: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
                .onSuccess {}
        }
    }
}





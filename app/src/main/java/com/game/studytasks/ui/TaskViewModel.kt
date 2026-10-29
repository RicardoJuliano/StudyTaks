package com.game.studytasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.studytasks.data.TaskRepository
import com.game.studytasks.shared.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            repository.getTask()
                .onSuccess { _tasks.value = it }

        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val taskAtualizada = task.copy(isDone = !task.isDone)
            repository.updateTask(task.id, taskAtualizada)
                .onSuccess { updated ->
                    _tasks.value = _tasks.value.map {
                        if (it.id == updated.id) updated else it
                    }
                }
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
                .onSuccess { created ->
                    _tasks.value = _tasks.value + created
                }
        }
    }
}





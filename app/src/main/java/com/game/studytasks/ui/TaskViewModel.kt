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
}





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
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            repository.observeTask().collect { _tasks.value = it }
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.refreshTask()
                .onFailure { _errorMessage.value = "Não foi possível sincronizar com o servidor. Mostrando dados salvos." }
            _isLoading.value = false
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val taskAtualizada = task.copy(isDone = !task.isDone)
            repository.updateTask(task.id, taskAtualizada)
                .onFailure { _errorMessage.value = "Não foi possível atualizar a task. Verifique sua conexão." }
            _isLoading.value = false
        }
    }
    fun createTask(title: String, category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val newTask = Task(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                category = category,
                isDone = false
            )
            repository.createTask(newTask)
                .onFailure { _errorMessage.value = "Não foi possível criar a task. Verifique sua conexão." }
            _isLoading.value = false
        }
    }
    fun deleteTask(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.deleteTask(id)
                .onFailure { _errorMessage.value = "Não foi possível deletar a task. Verifique sua conexão." }
            _isLoading.value = false
        }
    }
}





package com.game.studytasks.data

import com.game.studytasks.shared.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val dao: TaskDao) {

    private val api = TaskRetrofitClient.taskApiService


    suspend fun createTask(task: Task): Result<Task> {
        return try {
            val response = api.createTask(task)
            dao.insertAll(listOf(response.toEntity()))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(id: String, task: Task): Result<Task> {
        return try {
            val response = api.updateTask(id, task)
            dao.insertAll(listOf(response.toEntity()))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            val response = api.deleteTask(id)
            dao.delete(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeTask(): Flow<List<Task>> {
        return dao.getAll().map { entities -> entities.map { it.toTask() } }
    }

    suspend fun refreshTask(): Result<Unit> {
        return try {
            val response = api.getTask()
            dao.insertAll(response.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
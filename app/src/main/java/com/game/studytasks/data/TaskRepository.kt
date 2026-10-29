package com.game.studytasks.data

import com.game.studytasks.shared.Task

class TaskRepository {

    private val api = TaskRetrofitClient.taskApiService

    suspend fun getTask(): Result<List<Task>>
    {
        return try {
            val response = api.getTask()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun createTask(task: Task):Result<Task>
    {
        return try {
            val response = api.createTask(task)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateTask(id : String, task: Task):Result<Task>
    {
        return try {
            val response = api.updateTask(id, task)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteTask(id: String): Result<Unit>
    {
        return try {
            val response = api.deleteTask(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
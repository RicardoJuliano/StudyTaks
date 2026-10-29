package com.game.studytasks.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object TaskRetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://studytaks.onrender.com/")
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val taskApiService: TaskApiService = retrofit.create(TaskApiService::class.java)
}
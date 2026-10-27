package com.game.studytasks.shared

import kotlinx.serialization.Serializable


@Serializable
data class Task(
    val id: String,
    val title: String,
    val category: String,
    val isDone: Boolean
)

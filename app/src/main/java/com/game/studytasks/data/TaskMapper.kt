package com.game.studytasks.data

import com.game.studytasks.shared.Task


fun TaskEntity.toTask(): Task {
    return Task(id = id, title = title, category = category, isDone = isDone)
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(id = id, title = title, category = category, isDone = isDone)
}


package com.game.studytasks

import android.app.Application
import androidx.room.Room
import com.game.studytasks.data.AppDatabase

class StudyTasksApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "studytasks-db").build()
    }
}
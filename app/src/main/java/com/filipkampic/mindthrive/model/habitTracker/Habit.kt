package com.filipkampic.mindthrive.model.habitTracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val frequency: String = "",
    val reminder: String = "",
    val description: String = "",
    val isDoneToday: Boolean = false,
    val streak: Int = 0
)

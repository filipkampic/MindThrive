package com.filipkampic.mindthrive.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class Priority {
    HIGH, MEDIUM, LOW, NONE
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isDone: Boolean = false,
    val dueDate: LocalDate? = null,
    val priority: Priority = Priority.NONE,
    val category: String = "General"
)

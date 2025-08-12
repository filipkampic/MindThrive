package com.filipkampic.mindthrive.model.goals

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goalStep")
data class GoalStep(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int,
    val name: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val order: Int
)

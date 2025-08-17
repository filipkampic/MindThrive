package com.filipkampic.mindthrive.model.goals

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_note")
data class GoalNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int,
    val title: String,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long
)

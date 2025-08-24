package com.filipkampic.mindthrive.model.goals

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_category")
data class GoalCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

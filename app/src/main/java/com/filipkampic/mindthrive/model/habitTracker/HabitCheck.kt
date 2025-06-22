package com.filipkampic.mindthrive.model.habitTracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_checks")
data class HabitCheck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String,
    val isChecked: Boolean
)
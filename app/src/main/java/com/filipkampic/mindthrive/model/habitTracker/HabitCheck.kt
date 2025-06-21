package com.filipkampic.mindthrive.model.habitTracker

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_checks",
    indices = [Index(value = ["habitId", "date"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitCheck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String,
    val isChecked: Boolean? = null,
    val amount: Int? = null
)

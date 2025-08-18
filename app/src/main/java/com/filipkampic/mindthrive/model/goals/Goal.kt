package com.filipkampic.mindthrive.model.goals

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val motivation: String?,
    val reward: String?,
    val deadline: LocalDate,
    val category: String,
    val progress: Float = 0f,
    val completedAt: Long? = null,
    val isCompletedOnTime: Boolean? = null
)

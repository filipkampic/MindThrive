package com.filipkampic.mindthrive.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String,
    val name: String,
    val start: LocalTime?,
    val end: LocalTime?,
    val description: String = "",
    val date: LocalDate
) {
    fun duration(): String {
        return if (start != null && end != null) {
            val duration = Duration.between(start, end)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            when {
                hours == 0L -> "${minutes}min"
                minutes == 0L -> "${hours}h"
                else -> "${hours}h ${minutes}min"
            }
        } else {
            "N/A"
        }
    }
}

class TaskTypeConverters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, timeFormatter) }
    }
}
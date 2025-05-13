package com.filipkampic.mindthrive.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.LocalDateTime

@Entity(tableName = "timeBlocks")
data class TimeBlock(
    @PrimaryKey
    val id: String,
    val name: String,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
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

class TimeBlockTypeConverters {
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }
}
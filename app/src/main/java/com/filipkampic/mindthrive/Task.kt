package com.filipkampic.mindthrive

import java.time.LocalDate
import java.time.LocalTime

data class Task(
    val name: String,
    val start: LocalTime,
    val end: LocalTime,
    val description: String = "",
    val date: LocalDate
) {
    fun duration(): String {
        val minutes = java.time.Duration.between(start, end).toMinutes()
        val hours = minutes / 60
        val mins = minutes % 60
        return buildString {
            if (hours > 0) append("${hours}h ")
            if (mins > 0) append("${mins}min")
        }.trim()
    }
}
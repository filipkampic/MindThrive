package com.filipkampic.mindthrive.model.habitTracker

enum class FrequencyType { DAILY_INTERVAL, TIMES_PER_WEEK, TIMES_PER_MONTH }

data class HabitFrequency(
    val type: FrequencyType,
    val value: Int
)

fun parseFrequency(frequency: String?): HabitFrequency? {
    if (frequency == null) return null
    return when {
        frequency.startsWith("every") -> {
            val days = frequency.removePrefix("every ").removeSuffix(" days").toIntOrNull()
            if (days != null) HabitFrequency(FrequencyType.DAILY_INTERVAL, days) else null
        }
        frequency.endsWith("times per week") -> {
            val times = frequency.removeSuffix(" times per week").toIntOrNull()
            if (times != null) HabitFrequency(FrequencyType.TIMES_PER_WEEK, times) else null
        }
        frequency.endsWith("times per month") -> {
            val times = frequency.removeSuffix(" times per month").toIntOrNull()
            if (times != null) HabitFrequency(FrequencyType.TIMES_PER_MONTH, times) else null
        }
        else -> null
    }
}

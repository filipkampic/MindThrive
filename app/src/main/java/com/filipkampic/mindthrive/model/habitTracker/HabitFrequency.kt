package com.filipkampic.mindthrive.model.habitTracker

enum class FrequencyType { DAILY_INTERVAL, TIMES_PER_WEEK, TIMES_PER_MONTH }

data class HabitFrequency(
    val type: FrequencyType,
    val value: Int
)

fun parseFrequency(frequency: String?): HabitFrequency? {
    if (frequency == null) return HabitFrequency(FrequencyType.DAILY_INTERVAL, 1)

    val f = frequency.trim().lowercase()
    return when {
        f == "every day" -> HabitFrequency(FrequencyType.DAILY_INTERVAL, 1)
        f.startsWith("every ") && f.endsWith(" days") -> {
            val days = f.removePrefix("every ").removeSuffix(" days").toIntOrNull()
            if (days != null && days > 0) HabitFrequency(FrequencyType.DAILY_INTERVAL, days) else HabitFrequency(FrequencyType.DAILY_INTERVAL, 1)
        }
        f.endsWith(" times per week") -> {
            val times = f.removeSuffix(" times per week").toIntOrNull()
            if (times != null && times > 0) HabitFrequency(FrequencyType.TIMES_PER_WEEK, times) else null
        }
        f.endsWith(" times per month") -> {
            val times = f.removeSuffix(" times per month").toIntOrNull()
            if (times != null && times > 0) HabitFrequency(FrequencyType.TIMES_PER_MONTH, times) else null
        }
        else -> HabitFrequency(FrequencyType.DAILY_INTERVAL, 1)
    }
}

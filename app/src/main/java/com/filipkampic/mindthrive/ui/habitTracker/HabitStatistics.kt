package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.model.habitTracker.FrequencyType
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.model.habitTracker.HabitStats
import com.filipkampic.mindthrive.model.habitTracker.parseFrequency
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import java.time.LocalDate

@Composable
fun HabitStatistics(
    habit: Habit,
    viewModel: HabitViewModel
) {
    val checks by viewModel.getAllChecksForHabit(habit.id).collectAsState(initial = emptyList())
    val stats = remember(checks, habit) {
        calculateHabitStats(checks, habit)
    }

    val bestStreak = maxOf(stats.bestStreak, stats.currentStreak)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(DarkBlue, shape = RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Habit Statistics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Peach
        )
        StatRow(label = "Current streak", value = "${stats.currentStreak}")
        StatRow(label = "Best streak", value = "$bestStreak")
        StatRow(label = "Success rate", value = "${stats.successRate} %")
    }
}

@Composable
fun StatRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
        Text(value, color = Peach, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

fun calculateHabitStats(checks: List<HabitCheck>, habit: Habit): HabitStats {
    val today = LocalDate.now()
    val mutableMap = checks.associateBy { LocalDate.parse(it.date) }.toMutableMap()

    val todayCheck = mutableMap[today]
    if (todayCheck != null) {
        mutableMap[today] = todayCheck
    } else if (habit.isDoneToday && !habit.isMeasurable) {
        val pseudoCheck = HabitCheck(
            habitId = habit.id,
            date = today.toString(),
            isChecked = true,
            amount = null
        )
        mutableMap[today] = pseudoCheck
    }

    if (!mutableMap.containsKey(today) && habit.isDoneToday) {
        val pseudoCheck = HabitCheck(
            habitId = habit.id,
            date = today.toString(),
            isChecked = true,
            amount = if (habit.isMeasurable) habit.target?.toFloat() else null
        )
        mutableMap[today] = pseudoCheck
    }

    fun isSuccessful(check: HabitCheck): Boolean {
        if (!habit.isMeasurable) return check.isChecked

        val target = habit.target ?: return false
        val amount = check.amount ?: return false

        return when (habit.targetType?.lowercase()) {
            "at most" -> amount <= target
            else -> amount >= target
        }
    }

    val parsedFrequency = parseFrequency(habit.frequency)
    var currentStreak = 0
    var bestStreak = 0
    var tempStreak = 0
    var successCount = 0
    var totalExpected = 0

    when (parsedFrequency?.type) {
        FrequencyType.DAILY_INTERVAL -> {
            val interval = parsedFrequency.value
            var date = today
            while (true) {
                val check = mutableMap[date]
                if (date == today && (check == null || !isSuccessful(check))) {
                    date = date.minusDays(interval.toLong())
                    continue
                }
                if (check != null && isSuccessful(check)) {
                    currentStreak++
                    date = date.minusDays(interval.toLong())
                } else {
                    break
                }
            }

            val pastDays = (0..30).map { today.minusDays((it * interval).toLong()) }
            for (date in pastDays) {
                if (date == today && !habit.isDoneToday) continue
                val check = mutableMap[date]
                if (check != null && isSuccessful(check)) successCount++
                totalExpected++
            }

            date = today
            while (date.isAfter(today.minusDays(180))) {
                val check = mutableMap[date]
                if (check != null && isSuccessful(check)) {
                    tempStreak++
                    bestStreak = maxOf(bestStreak, tempStreak)
                } else {
                    tempStreak = 0
                }
                date = date.minusDays(interval.toLong())
            }
        }

        FrequencyType.TIMES_PER_WEEK -> {
            val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
            val endOfWeek = startOfWeek.plusDays(6)

            val weeklyChecks = mutableMap.filterKeys { it in startOfWeek..endOfWeek && (it != today || habit.isDoneToday) }
            successCount = weeklyChecks.count { isSuccessful(it.value) }
            totalExpected = parsedFrequency.value

            while (true) {
                val weekStart = startOfWeek.minusWeeks(currentStreak.toLong())
                val weekEnd = weekStart.plusDays(6)
                val weekSuccesses = mutableMap.filterKeys { it in weekStart..weekEnd }
                    .count { isSuccessful(it.value) }

                if (weekSuccesses >= parsedFrequency.value) {
                    currentStreak++
                } else break
            }

            var testBestStreak = 0
            while (true) {
                val weekStart = today.minusWeeks(testBestStreak.toLong())
                val weekEnd = weekStart.plusDays(6)
                val weekSuccesses = mutableMap.filterKeys { it in weekStart..weekEnd }
                    .count { isSuccessful(it.value) }
                if (weekSuccesses >= parsedFrequency.value) {
                    testBestStreak++
                    bestStreak = maxOf(bestStreak, testBestStreak)
                } else break
            }
        }

        FrequencyType.TIMES_PER_MONTH -> {
            val startOfMonth = today.withDayOfMonth(1)
            val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

            val monthlyChecks = mutableMap.filterKeys { it in startOfMonth..endOfMonth && (it != today || habit.isDoneToday) }
            successCount = monthlyChecks.count { isSuccessful(it.value) }
            totalExpected = parsedFrequency.value

            while (true) {
                val monthStart = startOfMonth.minusMonths(currentStreak.toLong())
                val monthEnd = monthStart.plusMonths(1).minusDays(1)
                val monthSuccesses = mutableMap.filterKeys { it in monthStart..monthEnd }
                    .count { isSuccessful(it.value) }
                if (monthSuccesses >= parsedFrequency.value) {
                    currentStreak++
                } else break
            }

            var testBestStreak = 0
            while (true) {
                val monthStart = today.minusMonths(testBestStreak.toLong()).withDayOfMonth(1)
                val monthEnd = monthStart.plusMonths(1).minusDays(1)
                val monthSuccesses = mutableMap.filterKeys { it in monthStart..monthEnd }
                    .count { isSuccessful(it.value) }
                if (monthSuccesses >= parsedFrequency.value) {
                    testBestStreak++
                    bestStreak = maxOf(bestStreak, testBestStreak)
                } else break
            }
        }

        else -> {
            var date = today
            while (true) {
                val check = mutableMap[date]
                if (date == today && (check == null || !isSuccessful(check))) {
                    date = date.minusDays(1)
                    continue
                }
                if (check != null && isSuccessful(check)) {
                    currentStreak++
                    date = date.minusDays(1)
                } else {
                    break
                }
            }

            for (check in checks.sortedBy { it.date }) {
                val date = LocalDate.parse(check.date)
                if (date == today && !habit.isDoneToday) continue

                if (isSuccessful(check)) {
                    tempStreak++
                    successCount++
                    bestStreak = maxOf(bestStreak, tempStreak)
                } else {
                    tempStreak = 0
                }
            }
            totalExpected = checks.count {
                val date = LocalDate.parse(it.date)
                date != today || habit.isDoneToday
            }
        }
    }

    val successRate = if (totalExpected > 0) (successCount * 100) / totalExpected else 0
    bestStreak = maxOf(bestStreak, currentStreak)
    return HabitStats(currentStreak, bestStreak, successRate)
}


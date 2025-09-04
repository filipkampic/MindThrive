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
import com.filipkampic.mindthrive.model.habitTracker.HabitFrequency
import com.filipkampic.mindthrive.model.habitTracker.HabitStats
import com.filipkampic.mindthrive.model.habitTracker.parseFrequency
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
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
            fontFamily = Montserrat,
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
        val target = habit.target?.toFloat() ?: return false
        val amount = check.amount ?: return false
        return when (habit.targetType?.lowercase()) {
            "at most" -> amount <= target
            else      -> amount >= target
        }
    }

    val parsedFrequency = parseFrequency(habit.frequency) ?: HabitFrequency(FrequencyType.DAILY_INTERVAL, 1)

    var currentStreak = 0
    var bestStreak = 0
    var tempStreak: Int
    var successCount = 0
    var totalExpected = 0

    val firstDate: LocalDate = mutableMap.keys.minOrNull() ?: today

    when (parsedFrequency.type) {
        FrequencyType.DAILY_INTERVAL -> {
            val interval = parsedFrequency.value

            var date = today
            while (!date.isBefore(firstDate)) {
                val check = mutableMap[date]
                if (date == today && (check == null || !isSuccessful(check))) {
                    date = date.minusDays(interval.toLong())
                    continue
                }
                if (check != null && isSuccessful(check)) {
                    currentStreak++
                    date = date.minusDays(interval.toLong())
                } else break
            }

            val pastDays = generateSequence(today) { it.minusDays(interval.toLong()) }
                .takeWhile { !it.isBefore(firstDate) }
                .toList()

            for (day in pastDays) {
                val c = mutableMap[day]
                if (c != null && isSuccessful(c)) successCount++
                totalExpected++
            }

            var scanDate = today
            tempStreak = 0
            while (!scanDate.isBefore(firstDate)) {
                val c = mutableMap[scanDate]
                if (c != null && isSuccessful(c)) {
                    tempStreak++
                    bestStreak = maxOf(bestStreak, tempStreak)
                } else {
                    tempStreak = 0
                }
                scanDate = scanDate.minusDays(interval.toLong())
            }
        }

        FrequencyType.TIMES_PER_WEEK -> {
            val targetPerWeek = parsedFrequency.value

            val firstWeekStart = firstDate.with(java.time.DayOfWeek.MONDAY)
            val currentWeekStart = today.with(java.time.DayOfWeek.MONDAY)
            val weeksCount = java.time.temporal.ChronoUnit.WEEKS.between(firstWeekStart, currentWeekStart).toInt() + 1

            successCount = 0
            totalExpected = 0

            repeat(weeksCount) { i ->
                val weekStart = currentWeekStart.minusWeeks(i.toLong())
                val weekEnd = weekStart.plusDays(6)
                val weekSuccesses = mutableMap
                    .filterKeys { it in weekStart..weekEnd && (it != today || habit.isDoneToday) }
                    .count { isSuccessful(it.value) }

                successCount += kotlin.math.min(weekSuccesses, targetPerWeek)
                totalExpected += targetPerWeek
            }

            var ws = currentWeekStart
            var streak = 0
            while (!ws.isBefore(firstWeekStart)) {
                val we = ws.plusDays(6)
                val cnt = mutableMap.filterKeys { it in ws..we }.count { isSuccessful(it.value) }
                if (cnt >= targetPerWeek) {
                    streak++
                    ws = ws.minusWeeks(1)
                } else break
            }
            currentStreak = streak

            var scanStart = currentWeekStart
            var running = 0
            while (!scanStart.isBefore(firstWeekStart)) {
                val scanEnd = scanStart.plusDays(6)
                val cnt = mutableMap.filterKeys { it in scanStart..scanEnd }.count { isSuccessful(it.value) }
                if (cnt >= targetPerWeek) {
                    running++
                    bestStreak = maxOf(bestStreak, running)
                } else {
                    running = 0
                }
                scanStart = scanStart.minusWeeks(1)
            }
        }

        FrequencyType.TIMES_PER_MONTH -> {
            val targetPerMonth = parsedFrequency.value

            val firstMonthStart = firstDate.withDayOfMonth(1)
            val currentMonthStart = today.withDayOfMonth(1)
            val monthsCount = java.time.temporal.ChronoUnit.MONTHS.between(firstMonthStart, currentMonthStart).toInt() + 1

            successCount = 0
            totalExpected = 0

            repeat(monthsCount) { i ->
                val monthStart = currentMonthStart.minusMonths(i.toLong())
                val monthEnd = monthStart.plusMonths(1).minusDays(1)
                val monthSuccesses = mutableMap
                    .filterKeys { it in monthStart..monthEnd && (it != today || habit.isDoneToday) }
                    .count { isSuccessful(it.value) }

                successCount += kotlin.math.min(monthSuccesses, targetPerMonth)
                totalExpected += targetPerMonth
            }

            var ms = currentMonthStart
            var streak = 0
            while (!ms.isBefore(firstMonthStart)) {
                val me = ms.plusMonths(1).minusDays(1)
                val cnt = mutableMap.filterKeys { it in ms..me }.count { isSuccessful(it.value) }
                if (cnt >= targetPerMonth) {
                    streak++
                    ms = ms.minusMonths(1)
                } else break
            }
            currentStreak = streak

            var scanStart = currentMonthStart
            var running = 0
            while (!scanStart.isBefore(firstMonthStart)) {
                val scanEnd = scanStart.plusMonths(1).minusDays(1)
                val cnt = mutableMap.filterKeys { it in scanStart..scanEnd }.count { isSuccessful(it.value) }
                if (cnt >= targetPerMonth) {
                    running++
                    bestStreak = maxOf(bestStreak, running)
                } else {
                    running = 0
                }
                scanStart = scanStart.minusMonths(1)
            }
        }
    }

    val successRate = if (totalExpected > 0) (successCount * 100) / totalExpected else 0
    bestStreak = maxOf(bestStreak, currentStreak)
    return HabitStats(currentStreak, bestStreak, successRate, successCount, totalExpected)
}


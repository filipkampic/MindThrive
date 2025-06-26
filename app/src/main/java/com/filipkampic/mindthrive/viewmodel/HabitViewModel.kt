package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.FrequencyType
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.model.habitTracker.HabitStats
import com.filipkampic.mindthrive.model.habitTracker.parseFrequency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    init {
        viewModelScope.launch {
            repository.getHabits().collect { list ->
                val updated = list.map { repository.resetIsDoneTodayIfNeeded(it) }
                _habits.value = updated
            }
        }
    }

    fun getHabitById(habitId: Int): Flow<Habit?> {
        return repository.getHabitById(habitId)
    }

    fun toggleHabit(habit: Habit) = viewModelScope.launch {
        val newIsDone = !habit.isDoneToday
        val newStreak = if (newIsDone) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)

        val updatedHabit = habit.copy(
            isDoneToday = newIsDone,
            streak = newStreak,
            lastUpdated = LocalDate.now().toString()
        )
        repository.insertHabit(updatedHabit)

        val today = LocalDate.now().toString()
        val existingCheck = repository.getCheck(habit.id, today)
        if (existingCheck == null) {
            repository.insertCheck(HabitCheck(habitId = habit.id, date = today, isChecked = newIsDone))
        } else {
            repository.insertCheck(existingCheck.copy(isChecked = newIsDone))
        }
    }

    fun markAllDone() = viewModelScope.launch {
        repository.markAllDone()
    }

    fun insertHabit(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
    }

    fun getAllChecksForHabit(habitId: Int): Flow<List<HabitCheck>> {
        return repository.getAllChecksForHabit(habitId)
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
                    if (check != null && isSuccessful(check)) {
                        currentStreak++
                        date = date.minusDays(interval.toLong())
                    } else {
                        break
                    }
                }

                val pastDays = (0..30).map { today.minusDays((it * interval).toLong()) }
                for (date in pastDays) {
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

                val weeklyChecks = mutableMap.filterKeys { it in startOfWeek..endOfWeek }
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

                val monthlyChecks = mutableMap.filterKeys { it in startOfMonth..endOfMonth }
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
                    if (check != null && isSuccessful(check)) {
                        currentStreak++
                        date = date.minusDays(1)
                    } else {
                        break
                    }
                }

                for (check in checks.sortedBy { it.date }) {
                    if (isSuccessful(check)) {
                        tempStreak++
                        successCount++
                        bestStreak = maxOf(bestStreak, tempStreak)
                    } else {
                        tempStreak = 0
                    }
                }
                totalExpected = checks.size
            }
        }

        val successRate = if (totalExpected > 0) (successCount * 100) / totalExpected else 0
        return HabitStats(currentStreak, bestStreak, successRate)
    }

    fun saveMeasurableCheck(habit: Habit, amount: Float) = viewModelScope.launch {
        val today = LocalDate.now().toString()
        val existingCheck = repository.getCheck(habit.id, today)

        val wasSuccessful = existingCheck?.let { check ->
            val amountValue = check.amount ?: return@let false
            when (habit.targetType?.lowercase()) {
                "at most" -> amountValue <= (habit.target?.toFloat() ?: Float.MAX_VALUE)
                else -> amountValue >= (habit.target?.toFloat() ?: 0f)

            }
        } ?: false

        val isNowSuccessful = when (habit.targetType?.lowercase()) {
            "at most" -> amount <= (habit.target?.toFloat() ?: Float.MAX_VALUE)
            else -> amount >= (habit.target?.toFloat() ?: 0f)
        }

        if (existingCheck == null) {
            repository.insertCheck(HabitCheck(habitId = habit.id, date = today, isChecked = true, amount = amount))
        } else {
            repository.insertCheck(existingCheck.copy(isChecked = true, amount = amount))
        }

        val newStreak = when {
            !wasSuccessful && isNowSuccessful -> habit.streak + 1
            wasSuccessful && !isNowSuccessful -> (habit.streak - 1).coerceAtLeast(0)
            else -> habit.streak
        }

        val updatedHabit = habit.copy(
            isDoneToday = isNowSuccessful,
            streak = newStreak,
            lastUpdated = today
        )

        repository.insertHabit(updatedHabit)
    }

    fun syncHabitStreaksWithChecks(checks: List<HabitCheck>) = viewModelScope.launch {
        _habits.value.forEach { habit ->
            val checksForHabit = checks.filter { it.habitId == habit.id }
            val stats = calculateHabitStats(checksForHabit, habit)

            if (
                habit.streak != stats.currentStreak ||
                habit.bestStreak != stats.bestStreak ||
                habit.successRate != stats.successRate
            ) {
                val updatedHabit = habit.copy(
                    streak = stats.currentStreak,
                    bestStreak = stats.bestStreak,
                    successRate = stats.successRate
                )
                repository.insertHabit(updatedHabit)
            }
        }
    }

    fun getAllChecks(): Flow<List<HabitCheck>> = repository.getAllChecks()
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

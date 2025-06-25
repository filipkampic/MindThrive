package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.model.habitTracker.HabitStats
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
        val sortedChecks = checks.sortedByDescending { it.date }
        val today = LocalDate.now()

        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0
        var successCount = 0

        val checkMap = sortedChecks.associateBy { LocalDate.parse(it.date) }

        fun isSuccessful(check: HabitCheck): Boolean {
            return if (habit.isMeasurable) {
                val target = habit.target ?: return false
                (check.amount ?: 0f) >= target
            } else {
                check.isChecked
            }
        }

        var date = today
        while (true) {
            val check = checkMap[date]
            if (check != null && isSuccessful(check)) {
                currentStreak++
                date = date.minusDays(1)
            } else {
                break
            }
        }

        for (check in sortedChecks.reversed()) {
            if (isSuccessful(check)) {
                tempStreak++
                successCount++
                bestStreak = maxOf(bestStreak, tempStreak)
            } else {
                tempStreak = 0
            }
        }

        val total = checks.size
        val successRate = if (total > 0) (successCount * 100) / total else 0

        return HabitStats(currentStreak, bestStreak, successRate)
    }


    fun saveMeasurableCheck(habit: Habit, amount: Float) = viewModelScope.launch {
        val today = LocalDate.now().toString()
        val existingCheck = repository.getCheck(habit.id, today)
        val target = habit.target ?: return@launch

        val wasSuccessful = existingCheck?.let { (it.amount ?: 0f) >= target } ?: false
        val isNowSuccessful = amount >= target

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

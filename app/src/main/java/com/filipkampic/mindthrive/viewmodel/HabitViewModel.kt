package com.filipkampic.mindthrive.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.model.habitTracker.HabitStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    fun calculateHabitStats(checks: List<HabitCheck>): HabitStats {
        val sortedChecks = checks.sortedBy { it.date }

        var bestStreak = 0
        var currentStreak = 0
        var successCount = 0

        for (check in sortedChecks) {
            if (check.isChecked) {
                currentStreak++
                successCount++
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak
                }
            } else {
                currentStreak = 0
            }
        }

        val total = checks.size
        val successRate = if (total > 0) (successCount * 100) / total else 0

        return HabitStats(currentStreak, bestStreak, successRate)
    }
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

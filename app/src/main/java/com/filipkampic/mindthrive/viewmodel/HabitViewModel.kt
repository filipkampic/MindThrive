package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.ui.habitTracker.calculateHabitStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    fun insertHabit(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repository.deleteHabit(habit)
        repository.deleteChecksForHabit(habit.id)
    }
    
    fun getAllChecksForHabit(habitId: Int): Flow<List<HabitCheck>> {
        return repository.getAllChecksForHabit(habitId)
    }

    fun saveMeasurableCheck(habit: Habit, amount: Float) = viewModelScope.launch {
        val today = LocalDate.now().toString()
        val existingCheck = repository.getCheck(habit.id, today)

        if (existingCheck == null) {
            repository.insertCheck(
                HabitCheck(
                    habitId = habit.id,
                    date = today,
                    isChecked = true,
                    amount = amount
                )
            )
        } else {
            repository.insertCheck(
                existingCheck.copy(
                    isChecked = true,
                    amount = amount
                )
            )
        }

        val checks = repository.getAllChecksForHabit(habit.id).first()
        val stats = calculateHabitStats(checks, habit)

        val bestStreak = maxOf(stats.bestStreak, stats.currentStreak)

        val updatedHabit = habit.copy(
            streak = stats.currentStreak,
            bestStreak = bestStreak,
            successRate = stats.successRate
        )

        if (habit.streak != stats.currentStreak ||
            habit.bestStreak != stats.bestStreak ||
            habit.successRate != stats.successRate
        ) {
            repository.updateHabit(updatedHabit)
        }
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
                val bestStreak = maxOf(stats.bestStreak, stats.currentStreak)

                val updatedHabit = habit.copy(
                    streak = stats.currentStreak,
                    bestStreak = bestStreak,
                    successRate = stats.successRate
                )
                repository.insertHabit(updatedHabit)
            }
        }
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

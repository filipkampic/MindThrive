package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {
    val habits: StateFlow<List<Habit>> = repository.getHabits()
        .map { it.sortedBy { habit -> habit.id } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleHabit(habit: Habit) = viewModelScope.launch {
        val newIsDone = !habit.isDoneToday
        val newStreak = if (newIsDone) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)

        val updatedHabit = habit.copy(
            isDoneToday = newIsDone,
            streak = newStreak
        )

        repository.insertHabit(updatedHabit)

        val todayDateStr = LocalDate.now().toString()
        toggleCheck(habit.id, todayDateStr, newIsDone)
    }

    fun markAllDone() = viewModelScope.launch {
        repository.markAllDone()
    }

    fun insertHabit(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
    }

    fun toggleCheck(habitId: Int, date: String, isChecked: Boolean) = viewModelScope.launch {
        val existingCheck = repository.getCheck(habitId, date)
        if (existingCheck == null) {
            repository.upsertCheck(HabitCheck(habitId = habitId, date = date, isChecked = isChecked))
        } else {
            repository.upsertCheck(existingCheck.copy(isChecked = isChecked))
        }

        if (date == LocalDate.now().toString()) {
            val currentHabits = habits.value
            val habit = currentHabits.find { it.id == habitId }
            if (habit != null && habit.isDoneToday != isChecked) {
                val newStreak = if (isChecked) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)
                val updatedHabit = habit.copy(isDoneToday = isChecked, streak = newStreak)
                repository.insertHabit(updatedHabit)
            }
        }
    }

    fun getCheckForDate(habitId: Int, date: String): Flow<HabitCheck?> {
        return repository.getCheckForDate(habitId, date)
    }

    fun getAllChecksForHabit(habitId: Int): Flow<List<HabitCheck>> {
        return repository.getAllChecksForHabit(habitId)
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

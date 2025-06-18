package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.Habit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {
    val habits: StateFlow<List<Habit>> = repository.getHabits()
        .map { it.sortedBy { habit -> habit.id } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleHabit(habit: Habit) = viewModelScope.launch {
        repository.toggleHabit(habit)
    }

    fun markAllDone() = viewModelScope.launch {
        repository.markAllDone()
    }

    fun startCreatingHabit(isYesOrNo: Boolean) {
        /* TODO: Start Creating Habit */
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

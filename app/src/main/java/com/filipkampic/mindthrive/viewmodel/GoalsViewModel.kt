package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GoalsViewModel(private val repository: GoalRepository) : ViewModel() {
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    val filteredGoals: StateFlow<List<Goal>> =
        combine(repository.getAllGoals(), selectedCategory) { goals, category ->
            if (category == "All") goals else goals.filter { it.category == category }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getAllCategories().collect { list ->
                val uniqueCategories = list.map { it.ifBlank { "General" } }.distinct()
                val allCategories = listOf("All", "General") + uniqueCategories.filterNot { it == "General" }
                _categories.value = allCategories
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun insertGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insert(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.update(goal)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.delete(goal)
        }
    }

    fun getGoalById(goalId: Int) : Flow<Goal?> {
        return repository.getGoalById(goalId)
    }

    fun isDeadlinePassed(deadline: LocalDate?) : Boolean {
        if (deadline == null) return false
        return LocalDate.now().isAfter(deadline)
    }

    fun calculateDaysRemaining(deadline: LocalDate?): Long {
        if (deadline == null) return Long.MAX_VALUE
        val today = LocalDate.now()
        if (today.isAfter(deadline)) { return 0 }
        return ChronoUnit.DAYS.between(today, deadline)
    }

    suspend fun goalNameExists(name: String, excludeId: Int? = null) : Boolean {
        return repository.getAllGoalsOnce().any { it.name.equals(name, ignoreCase = true) && it.id != excludeId }
    }
}

class GoalsViewModelFactory(private val repository: GoalRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            return GoalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
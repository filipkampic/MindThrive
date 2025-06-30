package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    suspend fun goalNameExists(name: String): Boolean {
        return repository.getAllGoalsOnce().any { it.name.equals(name, ignoreCase = true) }
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
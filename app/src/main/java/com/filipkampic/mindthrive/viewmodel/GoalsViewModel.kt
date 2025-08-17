package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.Goal
import com.filipkampic.mindthrive.model.goals.GoalNote
import com.filipkampic.mindthrive.model.goals.GoalProgress
import com.filipkampic.mindthrive.model.goals.GoalStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    private val _selectedGoalNote = MutableStateFlow<GoalNote?>(null)
    val selectedGoalNote: StateFlow<GoalNote?> = _selectedGoalNote.asStateFlow()

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

    fun getSteps(goalId: Int): Flow<List<GoalStep>> = repository.getStepsForGoal(goalId)

    fun addStep(goalId: Int, name: String, description: String?) {
        viewModelScope.launch {
            val currentSteps: List<GoalStep> = repository.getStepsForGoal(goalId).first()
            val nextOrder = currentSteps.size
            val newStep = GoalStep(
                goalId = goalId,
                name = name,
                description = description,
                isCompleted = false,
                order = nextOrder
            )
            repository.insertStep(newStep)
        }
    }

    fun updateStep(step: GoalStep) {
        viewModelScope.launch {
            repository.updateStep(step)
        }
    }

    fun deleteStep(step: GoalStep) {
        viewModelScope.launch {
            repository.deleteStep(step)
        }
    }

    fun updateStepsOrder(orderedSteps: List<GoalStep>) {
        viewModelScope.launch {
            repository.updateSteps(orderedSteps)
        }
    }

    fun updateGoalStepCompletion(step: GoalStep, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedStep = step.copy(isCompleted = isCompleted)
            repository.updateStep(updatedStep)
        }
    }

    fun goalProgress(goalId: Int) = getSteps(goalId).map { steps ->
        val total = steps.size
        val done = steps.count { it.isCompleted }
        val ratio = if (total == 0) 0f else done.toFloat() / total
        GoalProgress(done, total, ratio)
    }

    fun goalCompleted(goalId: Int) = getSteps(goalId).map { steps ->
        steps.isNotEmpty() && steps.all { it.isCompleted }
    }

    fun getNotes(goalId: Int): Flow<List<GoalNote>> = repository.getGoalNotes(goalId)

    fun upsertNote(goalId: Int, id: Int?, title: String, text: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (id == null) {
                repository.insertGoalNote(
                    GoalNote(
                        goalId = goalId,
                        title = title.trim(),
                        text = text.trim(),
                        createdAt = now,
                        updatedAt = now
                    )
                )
            } else {
                val existing = repository.getGoalNote(id) ?: return@launch
                repository.updateGoalNote(
                    existing.copy(
                        title = title.trim(),
                        text = text.trim(),
                        updatedAt = now
                    )
                )
            }
        }
    }

    fun deleteNote(note: GoalNote) {
        viewModelScope.launch { repository.deleteGoalNote(note) }
    }

    fun loadGoalNote(noteId: Int) {
        viewModelScope.launch {
            _selectedGoalNote.value = repository.getGoalNote(noteId)
        }
    }

    fun clearSelectedGoalNote() {
        _selectedGoalNote.value = null
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
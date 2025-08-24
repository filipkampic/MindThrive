package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.Goal
import com.filipkampic.mindthrive.model.goals.GoalCategory
import com.filipkampic.mindthrive.model.goals.GoalNote
import com.filipkampic.mindthrive.model.goals.GoalProgress
import com.filipkampic.mindthrive.model.goals.GoalStep
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class GoalsViewModel(private val repository: GoalRepository) : ViewModel() {
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = repository.getAllGoalCategories()
        .map { rows ->
            val uniqueCategories = rows.map { it.name.ifBlank { "General" } }
            val allCategories = listOf("All", "General") + uniqueCategories.filterNot { it.equals("General", true) }
            allCategories.distinctBy { it.lowercase() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeGoals: StateFlow<List<Goal>> =
        repository.getAllGoals().flatMapLatest { goals ->
            if (goals.isEmpty()) flowOf(emptyList())
            else combine(goals.map { g -> goalCompleted(g.id).map { done -> g to done }}) { pairs ->
                pairs.filter { !it.second }.map { it.first }
            }
        }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredGoals: StateFlow<List<Goal>> =
        combine(activeGoals, selectedCategory) { goals, category ->
            if (category == "All") goals else goals.filter { it.category == category }
        }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedGoals: StateFlow<List<Goal>> =
        repository.getAllGoals().flatMapLatest { goals ->
            if (goals.isEmpty()) flowOf(emptyList())
            else combine(goals.map { g -> goalCompleted(g.id).map { done -> g to done } }) { pairs ->
                pairs.filter { it.second }.map { it.first }
            }
        }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedGoalNote = MutableStateFlow<GoalNote?>(null)
    val selectedGoalNote: StateFlow<GoalNote?> = _selectedGoalNote.asStateFlow()

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
            val steps = repository.getStepsForGoal(goal.id).first()
            val allDone = steps.isNotEmpty() && steps.all { it.isCompleted }

            val adjusted = if (allDone) {
                val completedAt = goal.completedAt ?: System.currentTimeMillis()
                val onTime = goal.deadline.let { dl ->
                    Instant.ofEpochMilli(completedAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate() <= dl
                }

                goal.copy(
                    completedAt = completedAt,
                    isCompletedOnTime = onTime
                )
            } else {
                goal
            }

            repository.update(adjusted)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoalCascade(goal)
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

            val goalId = step.goalId
            val steps = repository.getStepsForGoal(goalId).first()
            val allDone = steps.isNotEmpty() && steps.all { it.isCompleted }

            val goal = repository.getGoalById(goalId).first() ?: return@launch

            if (allDone && goal.completedAt == null) {
                val isOnTime = !isDeadlinePassed(goal.deadline)
                repository.update(
                    goal.copy(
                        completedAt = System.currentTimeMillis(),
                        isCompletedOnTime = isOnTime
                    )
                )
            }
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

    suspend fun addCategory(categoryName: String): String? {
        if (categoryName.isBlank()) {
            return "Category name cannot be empty."
        }

        if (categoryName.equals("All", true) || categoryName.equals("General", true)) {
            return "\"All\" and \"General\" are reserved category names."
        }

        val existingCategory = repository.getCategoryByName(categoryName)
        if (existingCategory != null) {
            return "Category '$categoryName' already exists."
        }

        val newCategory = GoalCategory(name = categoryName)
        repository.insertCategory(newCategory.name)

        return null
    }

    suspend fun renameCategory(oldName: String, newNameRaw: String): String? {
        val newName = newNameRaw.trim()
        if (newName.isBlank()) return "Name cannot be empty"
        if (newName.equals("All", true) || newName.equals("General", true)) return "Reserved name"
        if (repository.getCategoryByName(newName) != null) return "Category already exists"

        val category = repository.getCategoryByName(oldName) ?: return "Category not found"
        repository.updateCategory(category.copy(name = newName))
        repository.renameCategoryInGoals(oldName, newName)

        if (_selectedCategory.value.equals(oldName, ignoreCase = true)) {
            _selectedCategory.value = newName
        }
        return null
    }

    suspend fun deleteCategory(name: String, deleteGoals: Boolean): String? {
        if (name.equals("All", true) || name.equals("General", true)) return "Cannot delete reserved category"

        if (deleteGoals) {
            repository.deleteCategoryAndGoalsCascade(name)
        } else {
            repository.moveGoalsFromCategoryToGeneral(name)
            val category = repository.getCategoryByName(name) ?: return "Category not found"
            repository.deleteCategory(category)
        }

        if (_selectedCategory.value.equals(name, ignoreCase = true)) {
            _selectedCategory.value = "All"
        }
        return null
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
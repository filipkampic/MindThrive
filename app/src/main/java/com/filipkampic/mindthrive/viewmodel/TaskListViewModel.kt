package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.tasks.TaskRepository
import com.filipkampic.mindthrive.model.tasks.Category
import com.filipkampic.mindthrive.model.tasks.SortDirection
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.tasks.TaskSortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskListViewModel(private val repository: TaskRepository): ViewModel() {
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val customCategories: StateFlow<List<String>> = repository.getCategories()
        .map { dbCategories ->
            val names = dbCategories.map { it.name }.toMutableSet()
            names += "General"
            listOf("General") + names.filterNot { it == "General" }.sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("General"))

    val categories = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _sortOption = MutableStateFlow(TaskSortOption.DEFAULT)
    val sortOption: StateFlow<TaskSortOption> = _sortOption
    private val _sortDirection = MutableStateFlow(SortDirection.ASCENDING)
    val sortDirection: StateFlow<SortDirection> = _sortDirection

    val showCompleted = repository.showCompletedFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val tasks: StateFlow<List<Task>> = combine(
        repository.allTasks,
        _selectedCategory,
        _sortOption,
        _sortDirection,
        repository.showCompletedFlow
    ) { allTasks, category, sort, direction, showCompleted ->
        val filteredByCategory = when (category) {
            "All" -> allTasks
            else -> allTasks.filter { it.category == category }
        }

        val filtered = if (showCompleted) {
            filteredByCategory
        } else {
            filteredByCategory.filter { !it.isDone }
        }

        val sorted = when (sort) {
            TaskSortOption.TITLE -> filtered.sortedBy { it.title.lowercase() }
            TaskSortOption.DUE_DATE -> filtered.sortedBy { it.dueDate ?: LocalDate.MAX }
            TaskSortOption.PRIORITY -> filtered.sortedBy { it.priority.ordinal }
            else -> filtered.sortedBy { it.position }
        }

        if (sort != TaskSortOption.DEFAULT && direction == SortDirection.DESCENDING) {
            sorted.reversed()
        } else {
            sorted
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTasks = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.sortOptionFlow.collect {
                _sortOption.value = it
            }
        }

        viewModelScope.launch {
            repository.sortDirectionFlow.collect {
                _sortDirection.value = it
            }
        }
    }


    fun addTask(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun toggleTask(task: Task) = viewModelScope.launch {
        repository.update(task.copy(isDone = !task.isDone))
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun updateTasksOrder(updated: List<Task>) = viewModelScope.launch {
        repository.updateTasksOrder(updated)
        _sortOption.value = TaskSortOption.DEFAULT
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addCategory(name: String) = viewModelScope.launch {
        val trimmed = name.trim()
        if (trimmed in listOf("All", "General")) return@launch
        repository.addCategory(Category(name = trimmed))
    }

    fun deleteCategory(name: String) = viewModelScope.launch {
        repository.deleteCategory(name)
        repository.reassignTasksFromCategory(name, to = "General")
    }

    fun reassignTasksFromCategory(from: String, to: String) = viewModelScope.launch {
        repository.reassignTasksFromCategory(from, to)
    }

    fun deleteTasksInCategory(category: String) = viewModelScope.launch {
        repository.deleteTasksInCategory(category)
    }

    fun setSortOption(option: TaskSortOption) = viewModelScope.launch {
        _sortOption.value = option
        repository.saveSortPreferences(option, _sortDirection.value)
    }

    fun toggleSortDirection() {
        _sortDirection.value = if (_sortDirection.value == SortDirection.ASCENDING)
            SortDirection.DESCENDING else SortDirection.ASCENDING
    }

    fun setSortDirection(direction: SortDirection) = viewModelScope.launch {
        _sortDirection.value = direction
        repository.saveSortPreferences(_sortOption.value, direction)
    }

    fun setShowCompleted(show: Boolean) = viewModelScope.launch {
        repository.saveShowCompleted(show)
    }
}
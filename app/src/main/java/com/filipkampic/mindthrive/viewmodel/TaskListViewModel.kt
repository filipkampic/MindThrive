package com.filipkampic.mindthrive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.TaskRepository
import com.filipkampic.mindthrive.model.tasks.Category
import com.filipkampic.mindthrive.model.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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


    val tasks = repository.allTasks
        .combine(_selectedCategory) { all, category ->
            if (category == "All") all
            else all.filter { it.category == category }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}
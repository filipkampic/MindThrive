package com.filipkampic.mindthrive.data.tasks

import android.content.Context
import com.filipkampic.mindthrive.model.tasks.Category
import com.filipkampic.mindthrive.model.tasks.SortDirection
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.tasks.TaskSortOption
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val context: Context,
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) {
    private val preferences = TaskPreferences(context)

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) = taskDao.insertTask(task)
    suspend fun update(task: Task) = taskDao.updateTask(task)
    suspend fun delete(task: Task) = taskDao.deleteTask(task)
    suspend fun updateTasksOrder(updated: List<Task>) = taskDao.updateTasks(updated)

    fun getCategories(): Flow<List<Category>> = categoryDao.getAll()
    suspend fun addCategory(category: Category) = categoryDao.insert(category)
    suspend fun deleteCategory(name: String) = categoryDao.deleteByName(name)
    suspend fun reassignTasksFromCategory(from: String, to: String) = taskDao.reassignCategory(from, to)
    suspend fun deleteTasksInCategory(category: String) = taskDao.deleteByCategory(category)

    val sortOptionFlow: Flow<TaskSortOption> = preferences.sortOptionFlow
    val sortDirectionFlow: Flow<SortDirection> = preferences.sortDirectionFlow
    val showCompletedFlow: Flow<Boolean> = preferences.showCompletedFlow

    suspend fun saveSortPreferences(option: TaskSortOption, direction: SortDirection) {
        preferences.saveSortPreferences(option, direction)
    }

    suspend fun saveShowCompleted(show: Boolean) {
        preferences.saveShowCompleted(show)
    }
}

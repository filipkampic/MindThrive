package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.filipkampic.mindthrive.dataStore
import com.filipkampic.mindthrive.model.tasks.Category
import com.filipkampic.mindthrive.model.tasks.SortDirection
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.tasks.TaskSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val context: Context,
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun insert(task: Task) = taskDao.insertTask(task)
    suspend fun update(task: Task) = taskDao.updateTask(task)
    suspend fun delete(task: Task) = taskDao.deleteTask(task)
    suspend fun updateTasksOrder(updated: List<Task>) = taskDao.updateTasks(updated)

    fun getCategories(): Flow<List<Category>> = categoryDao.getAll()
    suspend fun addCategory(category: Category) = categoryDao.insert(category)
    suspend fun deleteCategory(name: String) = categoryDao.deleteByName(name)
    suspend fun reassignTasksFromCategory(from: String, to: String) { taskDao.reassignCategory(from, to) }
    suspend fun deleteTasksInCategory(category: String) { taskDao.deleteByCategory(category) }

    val sortOptionFlow: Flow<TaskSortOption> = context.dataStore.data
        .map { it[SortPreferences.SORT_OPTION] ?: TaskSortOption.DEFAULT.name }
        .map { TaskSortOption.valueOf(it) }

    val sortDirectionFlow: Flow<SortDirection> = context.dataStore.data
        .map { it[SortPreferences.SORT_DIRECTION] ?: SortDirection.ASCENDING.name }
        .map { SortDirection.valueOf(it) }

    suspend fun saveSortPreferences(option: TaskSortOption, direction: SortDirection) {
        context.dataStore.edit { prefs ->
            prefs[SortPreferences.SORT_OPTION] = option.name
            prefs[SortPreferences.SORT_DIRECTION] = direction.name
        }
    }
}

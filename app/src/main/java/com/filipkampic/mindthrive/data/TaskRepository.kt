package com.filipkampic.mindthrive.data

import com.filipkampic.mindthrive.model.tasks.Category
import com.filipkampic.mindthrive.model.tasks.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(
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
}

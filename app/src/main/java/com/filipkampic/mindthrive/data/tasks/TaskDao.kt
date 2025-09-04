package com.filipkampic.mindthrive.data.tasks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.tasks.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isDone ASC, position ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Query("UPDATE tasks SET category = :to WHERE category = :from")
    suspend fun reassignCategory(from: String, to: String)

    @Query("DELETE FROM tasks WHERE category = :category")
    suspend fun deleteByCategory(category: String)
}
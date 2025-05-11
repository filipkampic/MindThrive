package com.filipkampic.mindthrive.data

import androidx.room.*
import com.filipkampic.mindthrive.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("""
    SELECT * FROM tasks 
    WHERE (:date = date OR 
          (start <= :endOfDay AND "end" > :startOfDay))
    ORDER BY start
""")
    fun getTasksByDate(date: LocalDate, startOfDay: String, endOfDay: String): Flow<List<Task>>



    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>
}
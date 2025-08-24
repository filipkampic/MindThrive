package com.filipkampic.mindthrive.data.goals

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.goals.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Query("UPDATE goals SET category = 'General' WHERE category = :categoryName")
    suspend fun moveGoalsToGeneral(categoryName: String)

    @Query("DELETE FROM goals WHERE category = :categoryName")
    suspend fun deleteGoalsInCategory(categoryName: String)

    @Query("SELECT DISTINCT category FROM goals")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalById(id: Int): Flow<Goal?>

    @Query("SELECT * FROM goals")
    suspend fun getAllGoalsOnce(): List<Goal>
}
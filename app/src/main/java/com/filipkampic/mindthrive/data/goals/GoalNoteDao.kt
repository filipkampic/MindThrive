package com.filipkampic.mindthrive.data.goals

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.goals.GoalNote
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalNoteDao {
    @Query("SELECT * FROM goal_note WHERE goalId = :goalId ORDER BY updatedAt DESC")
    fun getNotesForGoal(goalId: Int): Flow<List<GoalNote>>

    @Query("SELECT * FROM goal_note WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): GoalNote?

    @Insert
    suspend fun insert(note: GoalNote): Long

    @Update
    suspend fun update(note: GoalNote)

    @Delete
    suspend fun delete(note: GoalNote)

    @Query("DELETE FROM goal_note WHERE goalId = :goalId")
    suspend fun deleteNotesForGoal(goalId: Int)

    @Query("DELETE FROM goal_note WHERE goalId IN (SELECT id FROM goals WHERE category = :categoryName)")
    suspend fun deleteNotesFromCategory(categoryName: String)
}
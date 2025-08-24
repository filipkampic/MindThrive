package com.filipkampic.mindthrive.data.goals

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.goals.GoalStep
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalStepDao {
    @Query("SELECT * FROM GoalStep WHERE goalId = :goalId")
    fun getStepsForGoal(goalId: Int): Flow<List<GoalStep>>

    @Insert
    suspend fun insert(step: GoalStep)

    @Update
    suspend fun update(step: GoalStep)

    @Delete
    suspend fun delete(step: GoalStep)

    @Update
    suspend fun updateSteps(steps: List<GoalStep>)

    @Query("DELETE FROM goalStep WHERE goalId = :goalId")
    suspend fun deleteStepsForGoal(goalId: Int)

    @Query("DELETE FROM goalStep WHERE goalId IN (SELECT id FROM goals WHERE category = :categoryName)")
    suspend fun deleteStepsFromCategory(categoryName: String)
}
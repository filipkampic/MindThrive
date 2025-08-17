package com.filipkampic.mindthrive.data.goals

import com.filipkampic.mindthrive.model.goals.Goal
import com.filipkampic.mindthrive.model.goals.GoalNote
import com.filipkampic.mindthrive.model.goals.GoalStep
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao,
    private val goalStepDao: GoalStepDao,
    private val goalNoteDao: GoalNoteDao
) {
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    fun getAllCategories(): Flow<List<String>> = goalDao.getAllCategories()

    suspend fun insert(goal: Goal) = goalDao.insert(goal)

    suspend fun update(goal: Goal) = goalDao.update(goal)

    suspend fun delete(goal: Goal) = goalDao.delete(goal)

    fun getGoalById(id: Int): Flow<Goal?> = goalDao.getGoalById(id)

    suspend fun getAllGoalsOnce() : List<Goal> {
        return goalDao.getAllGoalsOnce()
    }

    fun getStepsForGoal(goalId: Int): Flow<List<GoalStep>> = goalStepDao.getStepsForGoal(goalId)

    suspend fun insertStep(step: GoalStep) = goalStepDao.insert(step)

    suspend fun updateStep(step: GoalStep) = goalStepDao.update(step)

    suspend fun deleteStep(step: GoalStep) = goalStepDao.delete(step)

    suspend fun updateSteps(steps: List<GoalStep>) = goalStepDao.updateSteps(steps)

    fun getGoalNotes(goalId: Int) = goalNoteDao.getNotesForGoal(goalId)

    suspend fun getGoalNote(id: Int)= goalNoteDao.getById(id)

    suspend fun insertGoalNote(note: GoalNote) = goalNoteDao.insert(note)

    suspend fun updateGoalNote(note: GoalNote) = goalNoteDao.update(note)

    suspend fun deleteGoalNote(note: GoalNote) = goalNoteDao.delete(note)
}

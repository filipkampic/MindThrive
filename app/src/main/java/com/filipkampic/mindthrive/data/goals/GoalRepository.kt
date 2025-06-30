package com.filipkampic.mindthrive.data.goals

import com.filipkampic.mindthrive.model.goals.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val dao: GoalDao) {
    fun getAllGoals(): Flow<List<Goal>> = dao.getAllGoals()

    fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()

    suspend fun insert(goal: Goal) = dao.insert(goal)

    suspend fun update(goal: Goal) = dao.update(goal)

    suspend fun delete(goal: Goal) = dao.delete(goal)

    suspend fun getGoalById(id: Int): Goal? = dao.getGoalById(id)

    suspend fun getAllGoalsOnce() : List<Goal> {
        return dao.getAllGoalsOnce()
    }
}

package com.filipkampic.mindthrive.data.habitTracker

import com.filipkampic.mindthrive.model.habitTracker.Habit
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    fun getHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    fun getHabitById(id: Int): Flow<Habit?> = habitDao.getHabitById(id)

    suspend fun markAllDone() = habitDao.markAllDone()

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
}
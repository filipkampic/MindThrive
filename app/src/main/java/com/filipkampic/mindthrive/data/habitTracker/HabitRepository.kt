package com.filipkampic.mindthrive.data.habitTracker

import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitCheckDao: HabitCheckDao
) {
    fun getHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun markAllDone() = habitDao.markAllDone()

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)

    fun getCheckForDate(habitId: Int, date: String): Flow<HabitCheck?> {
        return habitCheckDao.getCheckForDate(habitId, date)
    }

    fun getAllChecksForHabit(habitId: Int): Flow<List<HabitCheck>> {
        return habitCheckDao.getAllChecksForHabit(habitId)
    }

    suspend fun getCheck(habitId: Int, date: String): HabitCheck? {
        return habitCheckDao.getCheck(habitId, date)
    }

    suspend fun upsertCheck(check: HabitCheck) {
        habitCheckDao.upsertCheck(check)
    }
}
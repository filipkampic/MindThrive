package com.filipkampic.mindthrive.data.habitTracker

import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitCheckDao: HabitCheckDao
) {
    fun getHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    fun getHabitById(id: Int): Flow<Habit?> = habitDao.getHabitById(id)

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)

    fun getAllChecksForHabit(habitId: Int): Flow<List<HabitCheck>> = habitCheckDao.getChecksForHabit(habitId)

    suspend fun getCheck(habitId: Int, date: String): HabitCheck? {
        return habitCheckDao.getCheck(habitId, date)
    }

    suspend fun insertCheck(check: HabitCheck) {
        habitCheckDao.insertCheck(check)
    }

    suspend fun resetIsDoneTodayIfNeeded(habit: Habit): Habit {
        val today = LocalDate.now().toString()
        return if (habit.lastUpdated != today) {
            val updated = habit.copy(isDoneToday = false, lastUpdated = today)
            insertHabit(updated)
            updated
        } else habit
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.editHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.delete(habit)
    }

    suspend fun deleteChecksForHabit(habitId: Int) {
        habitCheckDao.deleteChecksByHabitId(habitId)
    }
}
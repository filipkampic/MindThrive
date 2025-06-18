package com.filipkampic.mindthrive.data.habitTracker

import com.filipkampic.mindthrive.model.habitTracker.Habit
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val dao: HabitDao) {
    fun getHabits(): Flow<List<Habit>> = dao.getAllHabits()

    suspend fun toggleHabit(habit: Habit) {
        val updated = habit.copy(
            isDoneToday = !habit.isDoneToday,
            streak = if (!habit.isDoneToday) habit.streak + 1 else maxOf(0, habit.streak - 1)
        )
        dao.editHabit(updated)
    }

    suspend fun markAllDone() = dao.markAllDone()

    suspend fun insertHabit(habit: Habit) = dao.insertHabit(habit)
}
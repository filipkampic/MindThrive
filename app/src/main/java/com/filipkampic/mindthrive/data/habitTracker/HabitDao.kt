package com.filipkampic.mindthrive.data.habitTracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.habitTracker.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Int): Flow<Habit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun editHabit(habit: Habit)

    @Query("UPDATE habits SET isDoneToday = 1, streak = streak + 1 WHERE isDoneToday = 0")
    suspend fun markAllDone()

    @Delete
    suspend fun delete(habit: Habit)

    @Update
    suspend fun updateHabits(habits: List<Habit>)
}
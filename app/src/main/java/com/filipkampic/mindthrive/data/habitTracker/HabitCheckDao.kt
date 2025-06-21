package com.filipkampic.mindthrive.data.habitTracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCheck(check: HabitCheck)

    @Query("SELECT * FROM habit_checks WHERE habitId = :habitId AND date = :date")
    fun getCheckForDate(habitId: Int, date: String): Flow<HabitCheck?>

    @Query("SELECT * FROM habit_checks WHERE habitId = :habitId ORDER BY date ASC")
    fun getAllChecksForHabit(habitId: Int): Flow<List<HabitCheck>>

    @Query("DELETE FROM habit_checks WHERE habitId = :habitId")
    suspend fun deleteChecksForHabit(habitId: Int)

    @Query("DELETE FROM habit_checks WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCheckForDate(habitId: Int, date: String)

    @Query("SELECT * FROM habit_checks WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCheck(habitId: Int, date: String): HabitCheck?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(habitCheck: HabitCheck)
}

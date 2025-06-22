package com.filipkampic.mindthrive.data.habitTracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCheckDao {
    @Query("SELECT * FROM habit_checks WHERE habitId = :habitId")
    fun getChecksForHabit(habitId: Int): Flow<List<HabitCheck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(habitCheck: HabitCheck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCheck(check: HabitCheck)

    @Query("SELECT * FROM habit_checks WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCheck(habitId: Int, date: String): HabitCheck?
}
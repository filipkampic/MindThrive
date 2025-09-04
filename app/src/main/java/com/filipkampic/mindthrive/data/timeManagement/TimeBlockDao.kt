package com.filipkampic.mindthrive.data.timeManagement

import androidx.room.*
import com.filipkampic.mindthrive.model.timeManagement.TimeBlock
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TimeBlockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeBlock(timeBlock: TimeBlock)

    @Update
    suspend fun updateTimeBlock(timeBlock: TimeBlock)

    @Delete
    suspend fun deleteTimeBlock(timeBlock: TimeBlock)

    @Query("""
    SELECT * FROM timeBlocks 
    WHERE (:date = date OR 
          (start <= :endOfDay AND "end" > :startOfDay))
    ORDER BY start
""")
    fun getTimeBlocksByDate(date: LocalDate, startOfDay: String, endOfDay: String): Flow<List<TimeBlock>>

    @Query("SELECT * FROM timeBlocks")
    fun getAllTimeBlocks(): Flow<List<TimeBlock>>

    @Query("""
    SELECT * FROM timeBlocks 
    WHERE start <= :endOfDay AND `end` > :startOfDay
    ORDER BY start
""")
    fun getTimeBlocksInRange(startOfDay: String, endOfDay: String): Flow<List<TimeBlock>>

    @Query("SELECT * FROM timeBlocks WHERE start > :now")
    suspend fun getAllFutureTimeBlocks(now: String = LocalDateTime.now().toString()): List<TimeBlock>
}
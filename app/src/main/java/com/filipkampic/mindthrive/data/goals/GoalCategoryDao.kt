package com.filipkampic.mindthrive.data.goals

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.goals.GoalCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalCategoryDao {
    @Query("SELECT * FROM goal_category ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<GoalCategory>>

    @Query("SELECT COUNT(*) FROM goal_category WHERE LOWER(name) = LOWER(:name)")
    suspend fun exists(name: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: GoalCategory)

    @Update
    suspend fun update(category: GoalCategory)

    @Delete
    suspend fun delete(category: GoalCategory)

    @Query("UPDATE goals SET category = :newName WHERE category = :oldName")
    suspend fun renameCategoryInGoals(oldName: String, newName: String)

    @Query("SELECT * FROM goal_category WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun getCategoryByName(name: String): GoalCategory?
}
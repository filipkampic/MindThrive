package com.filipkampic.mindthrive.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.filipkampic.mindthrive.model.tasks.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category)

    @Query("DELETE FROM categories WHERE name = :name")
    suspend fun deleteByName(name: String)
}
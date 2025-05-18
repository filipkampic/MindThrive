package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.filipkampic.mindthrive.model.Task
import com.filipkampic.mindthrive.model.TimeBlock
import com.filipkampic.mindthrive.model.TimeBlockTypeConverters

@TypeConverters(TimeBlockTypeConverters::class)
@Database(entities = [TimeBlock::class, Task::class], version=3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindthrive_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
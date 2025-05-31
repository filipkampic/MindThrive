package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.TimeBlock
import com.filipkampic.mindthrive.model.TimeBlockTypeConverters
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.model.tasks.Category

@TypeConverters(TimeBlockTypeConverters::class)
@Database(entities = [TimeBlock::class, Task::class, Category::class, Note::class], version=6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao

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
package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.filipkampic.mindthrive.data.habitTracker.HabitCheckDao
import com.filipkampic.mindthrive.data.habitTracker.HabitDao
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.TimeBlock
import com.filipkampic.mindthrive.model.TimeBlockTypeConverters
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.model.notes.NoteFolder
import com.filipkampic.mindthrive.model.tasks.Category

@TypeConverters(TimeBlockTypeConverters::class)
@Database(
    entities = [
        TimeBlock::class,
        Task::class,
        Category::class,
        Note::class,
        NoteFolder::class,
        Habit::class,
        HabitCheck::class
    ],
    version=12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCheckDao(): HabitCheckDao

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
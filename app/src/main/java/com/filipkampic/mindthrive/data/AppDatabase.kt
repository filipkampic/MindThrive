package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.filipkampic.mindthrive.data.goals.GoalCategoryDao
import com.filipkampic.mindthrive.data.goals.GoalDao
import com.filipkampic.mindthrive.data.goals.GoalNoteDao
import com.filipkampic.mindthrive.data.goals.GoalStepDao
import com.filipkampic.mindthrive.data.habitTracker.HabitCheckDao
import com.filipkampic.mindthrive.data.habitTracker.HabitDao
import com.filipkampic.mindthrive.data.notes.NoteDao
import com.filipkampic.mindthrive.data.tasks.CategoryDao
import com.filipkampic.mindthrive.data.tasks.TaskDao
import com.filipkampic.mindthrive.data.timeManagement.TimeBlockDao
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.timeManagement.TimeBlock
import com.filipkampic.mindthrive.model.timeManagement.TimeBlockTypeConverters
import com.filipkampic.mindthrive.model.goals.Goal
import com.filipkampic.mindthrive.model.goals.GoalCategory
import com.filipkampic.mindthrive.model.goals.GoalNote
import com.filipkampic.mindthrive.model.goals.GoalStep
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
        HabitCheck::class,
        Goal::class,
        GoalStep::class,
        GoalNote::class,
        GoalCategory::class
    ],
    version=24,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCheckDao(): HabitCheckDao
    abstract fun goalDao(): GoalDao
    abstract fun goalStepDao(): GoalStepDao
    abstract fun goalNoteDao(): GoalNoteDao
    abstract fun goalCategoryDao(): GoalCategoryDao

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
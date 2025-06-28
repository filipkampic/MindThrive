package com.filipkampic.mindthrive.notification.habitTracker

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.filipkampic.mindthrive.model.habitTracker.Habit
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

fun scheduleHabitReminder(context: Context, habit: Habit) {
    val triggerTime = try {
        LocalTime.parse(habit.reminder)
    } catch (e: Exception) {
        return
    }
    val now = LocalTime.now()
    var delay = Duration.between(now, triggerTime).toMillis()
    if (delay < 0) delay += Duration.ofDays(1).toMillis()

    val data = workDataOf("HABIT_NAME" to habit.name)
    val workManager = WorkManager.getInstance(context)

    workManager.cancelAllWorkByTag("habit_reminder_${habit.id}")

    val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag("habit_reminder_${habit.id}")
        .build()

    workManager.enqueue(request)
}

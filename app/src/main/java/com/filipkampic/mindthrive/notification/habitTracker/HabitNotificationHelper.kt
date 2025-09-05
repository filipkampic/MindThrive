package com.filipkampic.mindthrive.notification.habitTracker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.filipkampic.mindthrive.model.habitTracker.Habit
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

private fun uniqueWorkName(habitId: Int) = "habit_reminder_${habitId}"

fun scheduleHabitReminder(context: Context, habit: Habit) {
    val triggerTime = try {
        LocalTime.parse(habit.reminder)
    } catch (e: Exception) {
        return
    }
    val now = LocalTime.now()
    var delay = Duration.between(now, triggerTime).toMillis()
    if (delay < 0) delay += Duration.ofDays(1).toMillis()

    val data = workDataOf(
        "HABIT_ID" to habit.id,
        "HABIT_NAME" to habit.name
    )

    val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag(uniqueWorkName(habit.id))
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(uniqueWorkName(habit.id), ExistingWorkPolicy.REPLACE, workRequest)
}

fun cancelHabitReminder(context: Context, habitId: Int) {
    val wm = WorkManager.getInstance(context)
    wm.cancelUniqueWork(uniqueWorkName(habitId))
    wm.cancelAllWorkByTag(uniqueWorkName(habitId))
}

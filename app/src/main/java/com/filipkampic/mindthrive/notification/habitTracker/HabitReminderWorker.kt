package com.filipkampic.mindthrive.notification.habitTracker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.filipkampic.mindthrive.R
import com.filipkampic.mindthrive.main.MainActivity

class HabitReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val habitName = inputData.getString("HABIT_NAME") ?: return Result.failure()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("navigate_to", "habit")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, "habit_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("MindThrive Habit Reminder")
            .setContentText("Time for your habit: $habitName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}

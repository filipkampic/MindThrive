package com.filipkampic.mindthrive.notification.timeManagement

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.filipkampic.mindthrive.R
import com.filipkampic.mindthrive.main.MainActivity
import java.time.LocalDate

class TimeBlockNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val blockId = inputData.getString("BLOCK_ID") ?: return Result.failure()
        val blockName = inputData.getString("BLOCK_NAME") ?: return Result.failure()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "time_block_channel",
            "Time Block Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("navigate_to", "time")
            putExtra("date", LocalDate.now().toString())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "time_block_channel")
            .setContentTitle("MindThrive Reminder")
            .setContentText("It's time for: $blockName")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        val notificationId = blockId.hashCode()

        notificationManager.notify(notificationId, notification)

        return Result.success()
    }
}

package com.filipkampic.mindthrive.notification.timeManagement

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.filipkampic.mindthrive.model.timeManagement.TimeBlock
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private fun uniqueWorkName(blockId: String) = "timeBlock_$blockId"

fun scheduleTimeBlockNotification(context: Context, timeBlock: TimeBlock) {
    val start = timeBlock.start ?: return
    val now = LocalDateTime.now()
    val delayMillis = Duration.between(now, start).toMillis()

    if (delayMillis <= 0) return

    val workRequest = OneTimeWorkRequestBuilder<TimeBlockNotificationWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(
            workDataOf(
                "BLOCK_ID" to timeBlock.id,
                "BLOCK_NAME" to timeBlock.name
            )
        )
        .addTag(uniqueWorkName(timeBlock.id))
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(uniqueWorkName(timeBlock.id), ExistingWorkPolicy.REPLACE, workRequest)
}

fun cancelTimeBlockNotification(context: Context, blockId: String) {
    WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName(blockId))
}

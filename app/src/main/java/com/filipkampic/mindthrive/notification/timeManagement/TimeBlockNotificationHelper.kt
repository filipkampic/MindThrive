import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.filipkampic.mindthrive.model.TimeBlock
import com.filipkampic.mindthrive.notification.timeManagement.TimeBlockNotificationWorker
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

fun scheduleTimeBlockNotification(context: Context, timeBlock: TimeBlock) {
    val delayMillis = Duration.between(LocalTime.now(), timeBlock.start)
        .toMillis()
        .let { if (it < 0) 0 else it }

    val workRequest = OneTimeWorkRequestBuilder<TimeBlockNotificationWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(
            workDataOf("BLOCK_NAME" to timeBlock.name)
        )
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
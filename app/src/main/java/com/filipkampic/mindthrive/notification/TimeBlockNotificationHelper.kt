import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.filipkampic.mindthrive.model.TimeBlock
import com.filipkampic.mindthrive.notification.TimeBlockReceiver
import java.time.ZoneId

@SuppressLint("ScheduleExactAlarm")
fun scheduleTimeBlockNotification(context: Context, timeBlock: TimeBlock) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }
    }

    val intent = Intent(context, TimeBlockReceiver::class.java).apply {
        putExtra("BLOCK_NAME", timeBlock.name)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        timeBlock.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerAtMillis = timeBlock.start?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: return
    
    if (triggerAtMillis > System.currentTimeMillis()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
}
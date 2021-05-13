package com.nativework.covid19vaccinetracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.ui.HomeActivity
import com.nativework.covid19vaccinetracker.utils.Constants
import java.text.SimpleDateFormat
import java.util.*


class AppointmentNotification(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0x100, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val manager = from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID_PERIOD_WORK, Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
        val notification =
            NotificationCompat.Builder(context, Constants.CHANNEL_ID_PERIOD_WORK).apply {
                setContentIntent(pendingIntent)
            }
        notification.setContentTitle("Vaccine availability")
        notification.setContentText("There is vaccine available in your district")
        notification.priority = NotificationCompat.PRIORITY_HIGH
        notification.setCategory(NotificationCompat.CATEGORY_ALARM)
        notification.setSmallIcon(R.mipmap.ic_launcher)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        notification.setSound(sound)
        val vibrate = longArrayOf(0, 100, 200, 300)
        notification.setVibrate(vibrate)

        with(manager) {
            notify(getRandomNumber(), notification.build())
        }

    }
    private fun getRandomNumber(): Int {
        val dd = Date()
        val ft = SimpleDateFormat("mmssSS", Locale.ROOT)
        val s: String = ft.format(dd)
        return s.toInt()
    }

}
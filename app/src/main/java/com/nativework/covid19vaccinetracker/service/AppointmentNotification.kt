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
import com.nativework.covid19vaccinetracker.AgeGroups
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.models.Session
import com.nativework.covid19vaccinetracker.networks.NetworkModule
import com.nativework.covid19vaccinetracker.networks.NetworkService
import com.nativework.covid19vaccinetracker.ui.HomeActivity
import com.nativework.covid19vaccinetracker.ui.center.VaccineCenterActivity
import com.nativework.covid19vaccinetracker.utils.AppUtils
import com.nativework.covid19vaccinetracker.utils.Constants
import com.nativework.covid19vaccinetracker.utils.PreferenceConnector
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class AppointmentNotification(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val data = inputData.getString(Constants.DISTRICT_ID)
        val groupPref = AppUtils.readNotificationPref(context)
        Timber.d("Age limit preference %s", groupPref)
        Timber.d("data is received as districtID %s", data)
        val districtID: String =
            data ?: PreferenceConnector.readString(context, PreferenceConnector.DISTRICT_ID, "")
        if (districtID.isNotEmpty()) {
            // check for vaccine availability
            val networkModule = NetworkModule(applicationContext.cacheDir, context)
            val retrofitInstance = networkModule.provideCall().create(NetworkService::class.java)
            val call =
                retrofitInstance.getCenterListFromDistrict(districtID, AppUtils.getCurrentDate())
            val response = call.execute()
            if (response.isSuccessful) {
                Timber.d("Retrofit api called %s", response)
                val listOfCenters = response.body()?.centers
                if (!listOfCenters.isNullOrEmpty()) {
                    for (center in listOfCenters) {
                        val sessionsList = center.sessions
                        if (!sessionsList.isNullOrEmpty()) {
                            for (sessions in sessionsList) {
                                if (sessions.available_capacity!! > 0) {
                                    when (groupPref) {
                                        AgeGroups.ALL_GROUP.name -> {
                                            showNotification(center, sessions)
                                            return Result.success()
                                        }
                                        AgeGroups.AGE_18_44.name -> {
                                            if (sessions.min_age_limit == 18) {
                                                showNotification(center, sessions)
                                                return Result.success()
                                            }
                                        }
                                        AgeGroups.AGE_45_ALL.name -> {
                                            if (sessions.min_age_limit == 45) {
                                                showNotification(center, sessions)
                                                return Result.success()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                return Result.retry()
            }
        }
        return Result.success()
    }

    private fun showNotification(center: Center, sessions: Session) {
        val intent = Intent(context, VaccineCenterActivity::class.java).apply {
            putExtra(Constants.CENTER_DETAILS, center)
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
        notification.setContentText("${center.name} ")
        notification.priority = NotificationCompat.PRIORITY_HIGH
        notification.setCategory(NotificationCompat.CATEGORY_ALARM)
        notification.setSmallIcon(R.mipmap.ic_launcher)
        notification.setStyle(
            NotificationCompat.BigTextStyle().bigText(
                "The ${sessions.vaccine} vaccine is available " +
                        "at ${center.name}. The current available capacity is ${sessions.available_capacity}. " +
                        "The actual doses wise capacity is Dose 1: ${sessions.available_capacity_dose1} and Dose 2: ${sessions.available_capacity_dose2} " +
                        " Please confirm on CoWin portal to book your slot"
            )
        )
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
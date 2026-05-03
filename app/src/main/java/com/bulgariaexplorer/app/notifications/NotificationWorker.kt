package com.bulgariaexplorer.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bulgariaexplorer.app.MainActivity
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.RetrofitClient
import androidx.core.content.edit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "bulgaria_explorer_notifications"
        const val CHANNEL_NAME = "Известия"
        private const val PREFS_NAME = "notification_prefs"
        private const val LAST_NOTIFICATION_ID_KEY = "last_notification_id"
        private const val WORK_NAME = "notification_polling"
        private const val POLL_INTERVAL_MINUTES = 2L

        fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Известия за нови посещения, постижения и мисии"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        fun schedule(context: Context) {
            val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(POLL_INTERVAL_MINUTES, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val response = RetrofitClient.notificationApi.getUnreadNotifications()
            if (response.isSuccessful) {
                val notifications = response.body() ?: emptyList()
                val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastSeenId = prefs.getLong(LAST_NOTIFICATION_ID_KEY, 0L)

                val newNotifications = notifications.filter { it.id > lastSeenId }

                if (newNotifications.isNotEmpty()) {
                    newNotifications.forEach { notification ->
                        showNotification(notification.id.toInt(), notification.title, notification.message)
                    }
                    prefs.edit {
                        putLong(
                            LAST_NOTIFICATION_ID_KEY,
                            newNotifications.maxOf { it.id })
                    }
                }
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        } finally {
            schedule(applicationContext)
        }
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.notify(id, notification)
    }
}

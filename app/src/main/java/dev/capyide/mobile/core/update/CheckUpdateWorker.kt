package dev.capyide.mobile.core.update

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.capyide.mobile.CapyApp
import dev.capyide.mobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val app = applicationContext as? CapyApp
        val updateChecker = app?.container?.updateChecker ?: GithubUpdateChecker()
        val settingsRepository = app?.container?.settingsRepository

        return@withContext try {
            val settings = settingsRepository?.getSettings()
            if (settings != null && !settings.autoUpdateCheck) {
                Result.success()
            } else {
                val updateInfo = updateChecker.checkForUpdate()
                settingsRepository?.updateLastUpdateCheck(System.currentTimeMillis())

                if (updateInfo != null) {
                    showNotification(updateInfo)
                }
                Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(updateInfo: UpdateInfo) {
        if (!hasPostNotificationPermission()) return

        val manager = NotificationManagerCompat.from(applicationContext)
        if (!manager.areNotificationsEnabled()) {
            return
        }

        createChannel()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText("Update ${updateInfo.latestVersionName} is available")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(updateInfo.changelog)
            )
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun hasPostNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        val status = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        )
        return status == PackageManager.PERMISSION_GRANTED
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val WORK_NAME = "capy_update_check"
        private const val CHANNEL_ID = "capy_updates"
        private const val NOTIFICATION_ID = 1001
    }
}

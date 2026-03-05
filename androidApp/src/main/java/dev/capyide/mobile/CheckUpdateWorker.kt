package dev.capyide.mobile

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
import dev.capyide.mobile.core.config.SettingsRepository
import dev.capyide.mobile.core.update.UpdateChecker
import dev.capyide.mobile.core.update.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CheckUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val updateChecker: UpdateChecker by inject()
    private val settingsRepository: SettingsRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val settings = settingsRepository.getSettings()
            if (!settings.autoUpdateCheck) {
                Result.success()
            } else {
                val updateInfo = updateChecker.checkForUpdate()
                settingsRepository.updateLastUpdateCheck(System.currentTimeMillis())

                if (updateInfo != null) {
                    showNotification(updateInfo)
                }
                Result.success()
            }
        } catch (_: Exception) {
            Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(updateInfo: UpdateInfo) {
        if (!hasPostNotificationPermission()) return

        val manager = NotificationManagerCompat.from(applicationContext)
        if (!manager.areNotificationsEnabled()) return

        createChannel()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("CapyIDE Mobile")
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val WORK_NAME = "capy_update_check"
        private const val CHANNEL_ID = "capy_updates"
        private const val NOTIFICATION_ID = 1001
    }
}

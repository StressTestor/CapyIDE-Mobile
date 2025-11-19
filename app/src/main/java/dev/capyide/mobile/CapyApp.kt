package dev.capyide.mobile

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.capyide.mobile.core.config.EncryptedSettingsRepository
import dev.capyide.mobile.core.config.SettingsRepository
import dev.capyide.mobile.core.update.CheckUpdateWorker
import dev.capyide.mobile.core.update.GithubUpdateChecker
import dev.capyide.mobile.core.update.UpdateChecker
import java.util.concurrent.TimeUnit

class CapyApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        scheduleUpdateChecks()
    }

    private fun scheduleUpdateChecks() {
        val request = PeriodicWorkRequestBuilder<CheckUpdateWorker>(12, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CheckUpdateWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}

class AppContainer(context: Context) {
    val settingsRepository: SettingsRepository = EncryptedSettingsRepository(context)
    val updateChecker: UpdateChecker = GithubUpdateChecker()
}

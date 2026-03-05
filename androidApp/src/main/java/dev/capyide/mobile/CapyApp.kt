package dev.capyide.mobile

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.capyide.mobile.di.platformModule
import dev.capyide.mobile.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class CapyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CapyApp)
            modules(sharedModule, platformModule)
        }

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

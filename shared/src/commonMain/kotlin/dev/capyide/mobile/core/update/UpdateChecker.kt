package dev.capyide.mobile.core.update

interface UpdateChecker {
    suspend fun checkForUpdate(): UpdateInfo?
}

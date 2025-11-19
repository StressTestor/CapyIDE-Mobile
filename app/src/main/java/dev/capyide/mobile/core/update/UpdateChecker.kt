package dev.capyide.mobile.core.update

interface UpdateChecker {
    /**
     * Check for available updates from GitHub releases
     */
    suspend fun checkForUpdate(): UpdateInfo?
}

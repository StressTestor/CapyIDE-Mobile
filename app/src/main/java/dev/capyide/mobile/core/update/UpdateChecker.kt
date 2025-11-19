package dev.capyide.mobile.core.update

interface UpdateChecker {
    /**
     * Check for available updates from GitHub releases
     */
    suspend fun checkForUpdate(): UpdateInfo?
}

class StubUpdateChecker : UpdateChecker {
    override suspend fun checkForUpdate(): UpdateInfo? {
        // TODO: Implement real GitHub API call to check releases
        // For now return fake update info
        return UpdateInfo(
            latestVersionCode = 5,
            latestVersionName = "0.1.5",
            apkUrl = "https://github.com/example/capyide-mobile/releases/download/v0.1.5/app-debug.apk",
            changelog = """
                ## What's New
                - Initial scaffold complete
                - Basic navigation
                - Settings UI
                - AI provider stubs
                - Update checking stub
                
                Full changelog at GitHub releases page.
            """.trimIndent()
        )
    }
}
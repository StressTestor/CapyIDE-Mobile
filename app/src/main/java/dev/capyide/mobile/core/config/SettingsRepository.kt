package dev.capyide.mobile.core.config

import dev.capyide.mobile.core.ai.AiProviderType

interface SettingsRepository {
    /**
     * Get current app settings
     */
    suspend fun getSettings(): AppSettings
    
    /**
     * Update specific settings
     */
    suspend fun updateSettings(settings: AppSettings)
    
    /**
     * Update only specific fields
     */
    suspend fun updateApiKey(apiKey: String)
    suspend fun updateProvider(provider: AiProviderType)
    suspend fun updateAutoUpdateCheck(enabled: Boolean)
}

class StubSettingsRepository : SettingsRepository {
    // In-memory storage for stub implementation
    private var currentSettings = AppSettings()
    
    override suspend fun getSettings(): AppSettings {
        // TODO: Implement real persistence (DataStore, EncryptedSharedPreferences, etc.)
        return currentSettings
    }
    
    override suspend fun updateSettings(settings: AppSettings) {
        // TODO: Persist to secure storage
        currentSettings = settings
    }
    
    override suspend fun updateApiKey(apiKey: String) {
        // TODO: Encrypt and store securely
        currentSettings = currentSettings.copy(apiKey = apiKey)
    }
    
    override suspend fun updateProvider(provider: AiProviderType) {
        currentSettings = currentSettings.copy(selectedProvider = provider)
    }
    
    override suspend fun updateAutoUpdateCheck(enabled: Boolean) {
        currentSettings = currentSettings.copy(autoUpdateCheck = enabled)
    }
}
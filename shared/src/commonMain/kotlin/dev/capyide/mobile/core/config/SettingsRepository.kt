package dev.capyide.mobile.core.config

import dev.capyide.mobile.core.ai.AiProviderType
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<AppSettings>

    suspend fun getSettings(): AppSettings
    suspend fun updateSettings(settings: AppSettings)
    suspend fun updateApiKey(apiKey: String)
    suspend fun updateProvider(provider: AiProviderType)
    suspend fun updateAutoUpdateCheck(enabled: Boolean)
    suspend fun updateLastUpdateCheck(timestamp: Long)
}

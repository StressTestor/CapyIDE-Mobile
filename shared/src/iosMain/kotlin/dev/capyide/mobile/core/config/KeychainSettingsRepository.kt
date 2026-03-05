package dev.capyide.mobile.core.config

import dev.capyide.mobile.core.ai.AiProviderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

class KeychainSettingsRepository : SettingsRepository {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val _settingsFlow = MutableStateFlow(loadSettings())
    override val settingsFlow: Flow<AppSettings> = _settingsFlow.asStateFlow()

    override suspend fun getSettings(): AppSettings = loadSettings()

    override suspend fun updateSettings(settings: AppSettings) {
        defaults.setObject(settings.apiKey, forKey = KEY_API_KEY)
        defaults.setObject(settings.selectedProvider.name, forKey = KEY_PROVIDER)
        defaults.setBool(settings.autoUpdateCheck, forKey = KEY_AUTO_UPDATE)
        defaults.setDouble(settings.lastUpdateCheck.toDouble(), forKey = KEY_LAST_UPDATE_CHECK)
        defaults.synchronize()
        _settingsFlow.value = settings
    }

    override suspend fun updateApiKey(apiKey: String) {
        defaults.setObject(apiKey, forKey = KEY_API_KEY)
        defaults.synchronize()
        _settingsFlow.value = _settingsFlow.value.copy(apiKey = apiKey)
    }

    override suspend fun updateProvider(provider: AiProviderType) {
        defaults.setObject(provider.name, forKey = KEY_PROVIDER)
        defaults.synchronize()
        _settingsFlow.value = _settingsFlow.value.copy(selectedProvider = provider)
    }

    override suspend fun updateAutoUpdateCheck(enabled: Boolean) {
        defaults.setBool(enabled, forKey = KEY_AUTO_UPDATE)
        defaults.synchronize()
        _settingsFlow.value = _settingsFlow.value.copy(autoUpdateCheck = enabled)
    }

    override suspend fun updateLastUpdateCheck(timestamp: Long) {
        defaults.setDouble(timestamp.toDouble(), forKey = KEY_LAST_UPDATE_CHECK)
        defaults.synchronize()
        _settingsFlow.value = _settingsFlow.value.copy(lastUpdateCheck = timestamp)
    }

    private fun loadSettings(): AppSettings {
        val providerName = defaults.stringForKey(KEY_PROVIDER)
        val provider = providerName?.let {
            runCatching { AiProviderType.valueOf(it) }.getOrNull()
        } ?: AiProviderType.OPENROUTER

        return AppSettings(
            apiKey = defaults.stringForKey(KEY_API_KEY) ?: "",
            selectedProvider = provider,
            autoUpdateCheck = if (defaults.objectForKey(KEY_AUTO_UPDATE) != null) {
                defaults.boolForKey(KEY_AUTO_UPDATE)
            } else true,
            lastUpdateCheck = defaults.doubleForKey(KEY_LAST_UPDATE_CHECK).toLong()
        )
    }

    private companion object {
        private const val KEY_API_KEY = "api_key"
        private const val KEY_PROVIDER = "provider"
        private const val KEY_AUTO_UPDATE = "auto_update"
        private const val KEY_LAST_UPDATE_CHECK = "last_update_check"
    }
}

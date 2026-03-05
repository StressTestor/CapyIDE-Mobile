package dev.capyide.mobile.core.config

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.capyide.mobile.core.ai.AiProviderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class EncryptedSettingsRepository(context: Context) : SettingsRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    private val _settingsFlow = MutableStateFlow(loadSettings())
    override val settingsFlow: Flow<AppSettings> = _settingsFlow.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        _settingsFlow.value = loadSettings()
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override suspend fun getSettings(): AppSettings = withContext(Dispatchers.IO) {
        loadSettings()
    }

    override suspend fun updateSettings(settings: AppSettings) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(KEY_API_KEY, settings.apiKey)
                .putString(KEY_PROVIDER, settings.selectedProvider.name)
                .putBoolean(KEY_AUTO_UPDATE, settings.autoUpdateCheck)
                .putLong(KEY_LAST_UPDATE_CHECK, settings.lastUpdateCheck)
                .apply()
        }
    }

    override suspend fun updateApiKey(apiKey: String) {
        write { putString(KEY_API_KEY, apiKey) }
    }

    override suspend fun updateProvider(provider: AiProviderType) {
        write { putString(KEY_PROVIDER, provider.name) }
    }

    override suspend fun updateAutoUpdateCheck(enabled: Boolean) {
        write { putBoolean(KEY_AUTO_UPDATE, enabled) }
    }

    override suspend fun updateLastUpdateCheck(timestamp: Long) {
        write { putLong(KEY_LAST_UPDATE_CHECK, timestamp) }
    }

    private suspend fun write(block: SharedPreferences.Editor.() -> Unit) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                block()
                apply()
            }
        }
    }

    private fun loadSettings(): AppSettings {
        val providerName = sharedPreferences.getString(KEY_PROVIDER, null)
        val provider = providerName?.let {
            runCatching { AiProviderType.valueOf(it) }.getOrNull()
        } ?: AiProviderType.OPENROUTER

        return AppSettings(
            apiKey = sharedPreferences.getString(KEY_API_KEY, "") ?: "",
            selectedProvider = provider,
            autoUpdateCheck = sharedPreferences.getBoolean(KEY_AUTO_UPDATE, true),
            lastUpdateCheck = sharedPreferences.getLong(KEY_LAST_UPDATE_CHECK, 0L)
        )
    }

    private companion object {
        private const val PREFS_NAME = "capy_settings"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_PROVIDER = "provider"
        private const val KEY_AUTO_UPDATE = "auto_update"
        private const val KEY_LAST_UPDATE_CHECK = "last_update_check"
    }
}

package dev.capyide.mobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.capyide.mobile.CapyApp
import dev.capyide.mobile.core.ai.AiProviderType
import dev.capyide.mobile.core.config.AppSettings
import dev.capyide.mobile.core.config.SettingsRepository
import dev.capyide.mobile.core.update.UpdateChecker
import dev.capyide.mobile.core.update.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val apiKey: String = "",
    val selectedProvider: AiProviderType = AiProviderType.OPENROUTER,
    val autoUpdate: Boolean = true,
    val lastUpdateCheck: Long = 0L,
    val isSaving: Boolean = false,
    val isCheckingUpdate: Boolean = false,
    val message: String? = null,
    val latestUpdateInfo: UpdateInfo? = null,
    val errorMessage: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val updateChecker: UpdateChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        apiKey = settings.apiKey,
                        selectedProvider = settings.selectedProvider,
                        autoUpdate = settings.autoUpdateCheck,
                        lastUpdateCheck = settings.lastUpdateCheck,
                        message = null,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun updateApiKey(value: String) {
        _uiState.update { it.copy(apiKey = value) }
    }

    fun updateProvider(provider: AiProviderType) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    fun toggleAutoUpdate(enabled: Boolean) {
        _uiState.update { it.copy(autoUpdate = enabled) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, message = null, errorMessage = null) }
            try {
                val current = _uiState.value
                settingsRepository.updateSettings(
                    AppSettings(
                        apiKey = current.apiKey,
                        selectedProvider = current.selectedProvider,
                        autoUpdateCheck = current.autoUpdate,
                        lastUpdateCheck = current.lastUpdateCheck
                    )
                )
                _uiState.update { it.copy(isSaving = false, message = "Settings saved") }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Failed to save settings: ${e.localizedMessage}")
                }
            }
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingUpdate = true, message = null, errorMessage = null) }
            try {
                val updateInfo = updateChecker.checkForUpdate()
                val timestamp = System.currentTimeMillis()
                settingsRepository.updateLastUpdateCheck(timestamp)
                _uiState.update {
                    it.copy(
                        isCheckingUpdate = false,
                        message = updateInfo?.let { info ->
                            "Update ${info.latestVersionName} available"
                        } ?: "You're on the latest version",
                        latestUpdateInfo = updateInfo,
                        lastUpdateCheck = timestamp
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCheckingUpdate = false,
                        errorMessage = "Failed to check for updates: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(app: CapyApp): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(SettingsViewModel::class.java))
                    val repository = app.container.settingsRepository
                    val updateChecker = app.container.updateChecker
                    return SettingsViewModel(repository, updateChecker) as T
                }
            }
    }
}

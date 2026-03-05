package dev.capyide.mobile.viewmodel

import dev.capyide.mobile.core.ai.AiService
import dev.capyide.mobile.core.ai.ProviderConfig
import dev.capyide.mobile.core.config.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiAssistState(
    val isLoading: Boolean = false,
    val response: String = "",
    val error: String? = null
)

class AiAssistViewModel(
    private val aiService: AiService,
    private val settingsRepository: SettingsRepository
) {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(AiAssistState())
    val state: StateFlow<AiAssistState> = _state.asStateFlow()

    fun explain(code: String) {
        sendPrompt("Explain the following code concisely:\n\n```\n$code\n```")
    }

    fun complete(code: String) {
        sendPrompt("Complete the following code. Return only the completed code:\n\n```\n$code\n```")
    }

    fun refactor(code: String) {
        sendPrompt("Refactor the following code for clarity and efficiency. Return the refactored code:\n\n```\n$code\n```")
    }

    fun chat(message: String) {
        sendPrompt(message)
    }

    private fun sendPrompt(prompt: String) {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val settings = settingsRepository.getSettings()
                val config = ProviderConfig(apiKey = settings.apiKey)
                val result = aiService.makeRequest(
                    provider = settings.selectedProvider,
                    model = "auto",
                    prompt = prompt,
                    config = config
                )
                _state.update { it.copy(isLoading = false, response = result) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}

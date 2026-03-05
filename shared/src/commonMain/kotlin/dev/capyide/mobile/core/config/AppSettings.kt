package dev.capyide.mobile.core.config

import dev.capyide.mobile.core.ai.AiProviderType

data class AppSettings(
    val apiKey: String = "",
    val selectedProvider: AiProviderType = AiProviderType.OPENROUTER,
    val autoUpdateCheck: Boolean = true,
    val lastUpdateCheck: Long = 0L
)

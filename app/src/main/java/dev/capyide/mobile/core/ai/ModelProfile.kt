package dev.capyide.mobile.core.ai

data class ModelProfile(
    val name: String,
    val provider: AiProviderType,
    val maxTokens: Int,
    val contextWindow: Int,
    val supportedFeatures: List<String> = emptyList()
    // TODO: Add more model-specific configuration in future
)
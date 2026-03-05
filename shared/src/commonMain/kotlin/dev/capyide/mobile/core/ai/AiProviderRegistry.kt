package dev.capyide.mobile.core.ai

interface AiProviderRegistry {
    fun getModelProfiles(provider: AiProviderType): List<ModelProfile>
    fun getDefaultModel(provider: AiProviderType): ModelProfile?
    suspend fun makeRequest(
        provider: AiProviderType,
        model: ModelProfile,
        prompt: String,
        apiKey: String
    ): String
}

class StubAiProviderRegistry : AiProviderRegistry {
    override fun getModelProfiles(provider: AiProviderType): List<ModelProfile> {
        return listOf(
            ModelProfile(
                name = "stub-model",
                provider = provider,
                maxTokens = 4096,
                contextWindow = 128000,
                supportedFeatures = listOf("code-generation", "chat")
            )
        )
    }

    override fun getDefaultModel(provider: AiProviderType): ModelProfile? {
        return getModelProfiles(provider).firstOrNull()
    }

    override suspend fun makeRequest(
        provider: AiProviderType,
        model: ModelProfile,
        prompt: String,
        apiKey: String
    ): String {
        return "Stub response from ${provider.name} (${model.name}):\n\n" +
               "You entered: $prompt\n\n" +
               "// TODO: Real AI integration in future iteration"
    }
}

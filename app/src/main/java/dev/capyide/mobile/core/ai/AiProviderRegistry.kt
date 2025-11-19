package dev.capyide.mobile.core.ai

interface AiProviderRegistry {
    /**
     * Get available model profiles for a given provider
     */
    fun getModelProfiles(provider: AiProviderType): List<ModelProfile>
    
    /**
     * Get the default/recommended model for a provider
     */
    fun getDefaultModel(provider: AiProviderType): ModelProfile?
    
    /**
     * Make AI request (stubbed for now)
     */
    suspend fun makeRequest(
        provider: AiProviderType,
        model: ModelProfile,
        prompt: String,
        apiKey: String
    ): String
}

class StubAiProviderRegistry : AiProviderRegistry {
    override fun getModelProfiles(provider: AiProviderType): List<ModelProfile> {
        // TODO: Load real model catalog from config/network
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
        // TODO: Implement default model selection logic
        return getModelProfiles(provider).firstOrNull()
    }
    
    override suspend fun makeRequest(
        provider: AiProviderType,
        model: ModelProfile,
        prompt: String,
        apiKey: String
    ): String {
        // TODO: Implement real AI provider routing and HTTP calls
        return "Stub response from ${provider.name} (${model.name}):\n\n" +
               "You entered: $prompt\n\n" +
               "// TODO: Real AI integration in future iteration"
    }
}
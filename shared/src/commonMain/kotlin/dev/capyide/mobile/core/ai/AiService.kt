package dev.capyide.mobile.core.ai

import io.ktor.client.HttpClient

class AiService(private val client: HttpClient) {

    private val openRouterProvider = OpenAiCompatibleProvider(client, "https://openrouter.ai/api/v1")
    private val groqProvider = OpenAiCompatibleProvider(client, "https://api.groq.com/openai/v1")
    private val azureProvider = AzureOpenAiProvider(client)
    private val anthropicProvider = AnthropicProvider(client)

    suspend fun makeRequest(
        provider: AiProviderType,
        model: String,
        prompt: String,
        config: ProviderConfig
    ): String {
        return when (provider) {
            AiProviderType.OPENROUTER -> openRouterProvider.makeRequest(model, prompt, config.apiKey)
            AiProviderType.GROQ -> groqProvider.makeRequest(model, prompt, config.apiKey)
            AiProviderType.AZURE -> azureProvider.makeRequest(prompt, config)
            AiProviderType.ANTHROPIC -> anthropicProvider.makeRequest(prompt, config.apiKey, model)
        }
    }
}

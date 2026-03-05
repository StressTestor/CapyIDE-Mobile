package dev.capyide.mobile.core.ai

import dev.capyide.mobile.core.ai.models.OpenAiMessage
import dev.capyide.mobile.core.ai.models.OpenAiRequest
import dev.capyide.mobile.core.ai.models.OpenAiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AzureOpenAiProvider(
    private val client: HttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun makeRequest(
        prompt: String,
        config: ProviderConfig
    ): String {
        val url = "https://${config.resourceName}.openai.azure.com/openai/deployments/${config.deploymentName}/chat/completions?api-version=${config.apiVersion}"

        val request = OpenAiRequest(
            model = config.deploymentName,
            messages = listOf(
                OpenAiMessage(role = "user", content = prompt)
            )
        )

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("api-key", config.apiKey)
            setBody(json.encodeToString(OpenAiRequest.serializer(), request))
        }

        val body = response.bodyAsText()
        val parsed = json.decodeFromString(OpenAiResponse.serializer(), body)
        parsed.error?.let { throw RuntimeException(it.message) }
        return parsed.choices.firstOrNull()?.message?.content ?: ""
    }
}

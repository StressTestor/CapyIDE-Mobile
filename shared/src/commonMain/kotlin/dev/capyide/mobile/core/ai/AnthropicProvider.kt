package dev.capyide.mobile.core.ai

import dev.capyide.mobile.core.ai.models.AnthropicMessage
import dev.capyide.mobile.core.ai.models.AnthropicRequest
import dev.capyide.mobile.core.ai.models.AnthropicResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AnthropicProvider(
    private val client: HttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun makeRequest(prompt: String, apiKey: String, model: String = "claude-sonnet-4-20250514"): String {
        val request = AnthropicRequest(
            model = model,
            messages = listOf(
                AnthropicMessage(role = "user", content = prompt)
            )
        )

        val response = client.post("https://api.anthropic.com/v1/messages") {
            contentType(ContentType.Application.Json)
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            setBody(json.encodeToString(AnthropicRequest.serializer(), request))
        }

        val body = response.bodyAsText()
        val parsed = json.decodeFromString(AnthropicResponse.serializer(), body)
        parsed.error?.let { throw RuntimeException(it.message) }
        return parsed.content.firstOrNull { it.type == "text" }?.text ?: ""
    }
}

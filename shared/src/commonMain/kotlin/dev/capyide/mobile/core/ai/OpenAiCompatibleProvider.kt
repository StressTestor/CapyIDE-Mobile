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

class OpenAiCompatibleProvider(
    private val client: HttpClient,
    private val baseUrl: String
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun makeRequest(model: String, prompt: String, apiKey: String): String {
        val request = OpenAiRequest(
            model = model,
            messages = listOf(
                OpenAiMessage(role = "user", content = prompt)
            )
        )

        val response = client.post("$baseUrl/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(json.encodeToString(OpenAiRequest.serializer(), request))
        }

        val body = response.bodyAsText()
        val parsed = json.decodeFromString(OpenAiResponse.serializer(), body)
        parsed.error?.let { throw RuntimeException(it.message) }
        return parsed.choices.firstOrNull()?.message?.content ?: ""
    }
}

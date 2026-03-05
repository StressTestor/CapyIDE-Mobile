package dev.capyide.mobile.core.ai.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 4096,
    val temperature: Double = 0.7
)

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenAiResponse(
    val choices: List<OpenAiChoice> = emptyList(),
    val error: OpenAiError? = null
)

@Serializable
data class OpenAiChoice(
    val message: OpenAiMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class OpenAiError(
    val message: String,
    val type: String? = null
)

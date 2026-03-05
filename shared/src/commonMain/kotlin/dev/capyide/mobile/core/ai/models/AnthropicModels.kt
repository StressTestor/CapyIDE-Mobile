package dev.capyide.mobile.core.ai.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnthropicRequest(
    val model: String,
    val messages: List<AnthropicMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 4096
)

@Serializable
data class AnthropicMessage(
    val role: String,
    val content: String
)

@Serializable
data class AnthropicResponse(
    val content: List<AnthropicContent> = emptyList(),
    val error: AnthropicError? = null
)

@Serializable
data class AnthropicContent(
    val type: String,
    val text: String? = null
)

@Serializable
data class AnthropicError(
    val type: String,
    val message: String
)

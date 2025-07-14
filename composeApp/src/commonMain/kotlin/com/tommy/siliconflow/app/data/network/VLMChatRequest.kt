package com.tommy.siliconflow.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VLMChatRequest(
    val model: String,
    val messages: List<VLMMessage>,
    val stream: Boolean = true,
    @SerialName("max_tokens")
    val maxTokens: Int,
)

@Serializable
data class VLMMessage(
    val role: String,
    val content: List<VLMContent>,
)

@Serializable
data class VLMContent(
    val type: String,
    @SerialName("image_url")
    val imageUrl: ImageContent? = null,
    val text: String? = null,
)

@Serializable
data class ImageContent(
    val detail: String? = null,
    val url: String,
)
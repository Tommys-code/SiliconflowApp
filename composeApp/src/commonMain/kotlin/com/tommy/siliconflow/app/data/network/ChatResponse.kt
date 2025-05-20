package com.tommy.siliconflow.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    @SerialName("finish_reason")
    val finishReason: String?,
    val delta: ChoiceDelta,
)

@Serializable
data class ChoiceDelta(
    val content: String?,
    @SerialName("reasoning_content")
    val reasoningContent: String?,
    val role: String,
)
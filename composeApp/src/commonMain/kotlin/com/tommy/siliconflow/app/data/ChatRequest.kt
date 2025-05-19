package com.tommy.siliconflow.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String = "Qwen/Qwen3-8B",
    val messages: List<Message>,
    val stream: Boolean = true,
    @SerialName("max_tokens")
    val maxTokens: Int = 8192,
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)
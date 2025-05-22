package com.tommy.siliconflow.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = true,
    @SerialName("max_tokens")
    val maxTokens: Int,
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)
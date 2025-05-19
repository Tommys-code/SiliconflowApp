package com.tommy.siliconflow.app.network.service

import com.tommy.siliconflow.app.data.ChatRequest
import com.tommy.siliconflow.app.data.ChatResponse
import com.tommy.siliconflow.app.data.Message
import com.tommy.siliconflow.app.extensions.sseChat
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow

class SSEService(private val client: HttpClient) {

    private val test = ChatRequest(
        messages = listOf(
            Message(
                "user",
                "1+1=?"
            )
        ),
        maxTokens = 512,
    )

    suspend fun chatCompletions(data: String): Flow<ChatResponse> {
        return client.sseChat(
            "/chat/completions",
            ChatRequest(
                messages = listOf(Message("user", data))
            )
        )
    }
}
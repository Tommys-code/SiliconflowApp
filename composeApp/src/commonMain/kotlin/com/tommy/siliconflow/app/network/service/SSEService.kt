package com.tommy.siliconflow.app.network.service

import com.tommy.siliconflow.app.data.network.ChatRequest
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.extensions.sseChat
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SSEService(private val client: HttpClient) {

    suspend fun chatCompletions(chatRequest: ChatRequest): Flow<ChatResult<ChatResponse>> {
        return client.sseChat(
            "/chat/completions",
            chatRequest,
        )
    }
}
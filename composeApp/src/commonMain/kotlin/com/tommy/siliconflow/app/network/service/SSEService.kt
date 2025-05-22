package com.tommy.siliconflow.app.network.service

import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.network.ChatRequest
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.network.Message
import com.tommy.siliconflow.app.datasbase.ModelStore
import com.tommy.siliconflow.app.extensions.sseChat
import com.tommy.siliconflow.app.model.LocalAITextModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SSEService(
    private val client: HttpClient,
    private val modelStore: ModelStore,
) {

    suspend fun chat(messages: List<Message>): Flow<ChatResult<ChatResponse>> {
        modelStore.currentModel.first().let {
            when (it) {
                is LocalAITextModel -> {
                    return chatCompletions(
                        ChatRequest(
                            model = it.model,
                            messages = messages,
                            maxTokens = it.maxTokens,
                        ),
                    )
                }

                else -> {
                    throw IllegalArgumentException("Unsupported model type")
                }
            }
        }
    }

    private suspend fun chatCompletions(chatRequest: ChatRequest): Flow<ChatResult<ChatResponse>> {
        return client.sseChat(
            "/chat/completions",
            chatRequest,
        )
    }
}
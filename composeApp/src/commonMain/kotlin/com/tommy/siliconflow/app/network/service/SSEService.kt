package com.tommy.siliconflow.app.network.service

import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.VLMImageData
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.network.ChatRequest
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.network.Message
import com.tommy.siliconflow.app.data.network.VLMChatRequest
import com.tommy.siliconflow.app.data.network.VLMContent
import com.tommy.siliconflow.app.data.network.VLMMessage
import com.tommy.siliconflow.app.data.toVLMContent
import com.tommy.siliconflow.app.datasbase.ModelStore
import com.tommy.siliconflow.app.extensions.sseChat
import com.tommy.siliconflow.app.model.TextAIType
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SSEService(
    private val client: HttpClient,
    private val modelStore: ModelStore,
) {

    suspend fun chat(
        data: String,
        imageData: List<VLMImageData>? = null
    ): Flow<ChatResult<ChatResponse>> {
        modelStore.currentModel.first().let {
            if (it.type == TextAIType.VLM) {
                val send = VLMChatRequest(
                    model = it.model,
                    messages = listOf(
                        VLMMessage(
                            role = Role.USER.value,
                            content = imageData.toVLMContent().plus(
                                VLMContent(
                                    type = "text",
                                    text = data,
                                )
                            )
                        )
                    ),
                    maxTokens = it.maxTokens,
                )
                return chatCompletions(send)
            } else {
                return chatCompletions(
                    ChatRequest(
                        model = it.model,
                        messages = listOf(Message(role = Role.USER.value, content = data)),
                        maxTokens = it.maxTokens,
                    ),
                )
            }
        }
    }

    private suspend inline fun <reified T> chatCompletions(chatRequest: T): Flow<ChatResult<ChatResponse>> {
        return client.sseChat(
            "/chat/completions",
            chatRequest,
        )
    }
}
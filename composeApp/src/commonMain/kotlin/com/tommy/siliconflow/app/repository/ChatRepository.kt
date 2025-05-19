package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.ChatResponse
import com.tommy.siliconflow.app.network.service.SSEService
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val sseService: SSEService) {

    suspend fun sendData(data:String): Flow<ChatResponse> {
        return sseService.chatCompletions(data)
    }
}
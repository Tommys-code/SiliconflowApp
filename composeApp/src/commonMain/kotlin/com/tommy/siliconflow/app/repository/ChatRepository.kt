package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.ChatResponse
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.datasbase.ChatHistoryStore
import com.tommy.siliconflow.app.network.service.SSEService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ChatRepository(
    private val sseService: SSEService,
    private val chatHistoryStore: ChatHistoryStore,
    private val scope: CoroutineScope,
) {

    val sessionList = chatHistoryStore.getSessionList()

    private val _currentSession = MutableStateFlow<Long?>(null)
    val currentSession: StateFlow<Long?> = _currentSession

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatHistory: Flow<List<ChatHistory>> = currentSession.flatMapLatest { id ->
        id?.let { chatHistoryStore.getChatHistory(it) } ?: emptyFlow()
    }

    init {
        scope.launch { _currentSession.value = sessionList.conflate().first().getOrNull(0)?.id }
    }

    suspend fun sendData(data: String) {
        currentSession.value?.let {
            chatHistoryStore.insertSendHistory(it, data)
        } ?: run {
            chatHistoryStore.createSession(data).let {
                _currentSession.value = it
            }
        }
//        return sseService.chatCompletions(data)
    }
}
package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.ChatRequest
import com.tommy.siliconflow.app.data.ChatResponse
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.Message
import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.datasbase.ChatHistoryStore
import com.tommy.siliconflow.app.extensions.toChatContent
import com.tommy.siliconflow.app.extensions.toChatContentResult
import com.tommy.siliconflow.app.network.service.SSEService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatRepository(
    private val sseService: SSEService,
    private val chatHistoryStore: ChatHistoryStore,
    private val scope: CoroutineScope,
) {

    val sessionList = chatHistoryStore.getSessionList()

    private val _currentSession = MutableStateFlow<Session?>(null)
    private val currentSession: StateFlow<Session?> = _currentSession

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatHistory: Flow<List<ChatHistory>> = currentSession.flatMapLatest { session ->
        session?.let { chatHistoryStore.getChatHistory(it.id) } ?: emptyFlow()
    }

    private val _answer = MutableStateFlow<ChatResult<ChatContent>?>(null)
    val answer: StateFlow<ChatResult<ChatContent>?> = _answer

    init {
        scope.launch { _currentSession.value = sessionList.conflate().first().getOrNull(0) }
    }

    suspend fun sendData(data: String) {
        currentSession.value?.let {
            chatHistoryStore.insertSendHistory(it.id, data)
        } ?: run {
            chatHistoryStore.createSession(data).let { _currentSession.value = it }
        }
        _answer.emit(ChatResult.Start)
        sseService.chatCompletions(ChatRequest(messages = listOf(Message(Role.USER.value, data)))).collect {
            updateAnswer(it)
        }
    }

    private suspend fun updateAnswer(data: ChatResult<ChatResponse>) {
        if (data is ChatResult.Finish) {
            (_answer.value as? ChatResult.Progress)?.let {
                chatHistoryStore.insertReceiveHistory(currentSession.value!!.id, it.data)
            }
        }

        data.toChatContentResult()?.let { cur ->
            if (cur is ChatResult.Progress && _answer.value is ChatResult.Progress) {
                (_answer.value as ChatResult.Progress).data.let {
                    _answer.emit(ChatResult.Progress(it.copy(content = it.content + cur.data.content)))
                }
            } else {
                _answer.emit(cur)
            }
        }
    }
}
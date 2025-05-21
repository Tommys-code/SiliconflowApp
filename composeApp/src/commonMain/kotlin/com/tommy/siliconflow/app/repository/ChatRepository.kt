package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.network.ChatRequest
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.network.Message
import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.datasbase.ChatHistoryStore
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.extensions.toChatContentResult
import com.tommy.siliconflow.app.network.service.SSEService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatRepository(
    private val sseService: SSEService,
    private val chatHistoryStore: ChatHistoryStore,
    settingDataStore: SettingDataStore,
    scope: CoroutineScope,
) {

    private val useID = settingDataStore.getUserInfo().map { it?.id }

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessionList: Flow<List<Session>> = useID.flatMapLatest { id ->
        id?.let { chatHistoryStore.getSessionList(it) } ?: flowOf()
    }

    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatHistory: Flow<List<ChatHistory>> = currentSession.flatMapLatest { session ->
        session?.let { chatHistoryStore.getChatHistory(it.id) } ?: flowOf(emptyList())
    }

    private val _answer = MutableStateFlow<ChatResult<ChatContent>?>(null)
    val answer: StateFlow<ChatResult<ChatContent>?> = _answer

    init {
        scope.launch { _currentSession.value = sessionList.conflate().first().getOrNull(0) }
    }

    suspend fun sendData(data: String) {
        val chatID: Long = currentSession.value?.let {
            chatHistoryStore.insertSendHistory(it.id, data)
        } ?: run {
            chatHistoryStore.createSession(useID.conflate().first().orEmpty(), data).let {
                _currentSession.value = it.first
                it.second
            }
        }
        _answer.emit(ChatResult.Start)
        sseService.chatCompletions(ChatRequest(messages = listOf(Message(Role.USER.value, data)))).collect {
            updateAnswer(it, chatID)
        }
    }

    fun changeSession(session: Session?) {
        _currentSession.value = session
    }

    private suspend fun updateAnswer(data: ChatResult<ChatResponse>, chatID: Long) {
        if (data is ChatResult.Finish) {
            (_answer.value as? ChatResult.Progress)?.let {
                chatHistoryStore.updateReceiveHistory(chatID, it.data)
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

    suspend fun changeSessionName(session: Session): Boolean {
        return chatHistoryStore.updateSession(session)
    }

    suspend fun deleteSession(session: List<Session>): Boolean {
        return chatHistoryStore.deleteSession(session)
    }
}
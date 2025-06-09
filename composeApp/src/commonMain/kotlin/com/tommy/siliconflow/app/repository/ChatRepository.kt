package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.MarkdownChatHistory
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.data.db.SessionType
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.network.ChoiceDelta
import com.tommy.siliconflow.app.data.network.Message
import com.tommy.siliconflow.app.datasbase.ChatHistoryStore
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.extensions.appendContent
import com.tommy.siliconflow.app.extensions.toChatContent
import com.tommy.siliconflow.app.extensions.toChatContentResult
import com.tommy.siliconflow.app.extensions.toMarkdownChatHistory
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
import kotlinx.coroutines.flow.mapLatest
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

    private val _answer = MutableStateFlow<ChatResult<ChoiceDelta>?>(null)
    val answer: StateFlow<ChatResult<ChoiceDelta>?> = _answer

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatHistory: Flow<List<MarkdownChatHistory>> = currentSession.flatMapLatest { session ->
        session?.let { chatHistoryStore.getChatHistory(it.id) } ?: flowOf(emptyList())
    }.mapLatest {
        it.map { chatHistory ->
            chatHistory.toMarkdownChatHistory().apply { _answer.emit(null) }
        }
    }

    init {
        scope.launch {
            _currentSession.value =
                sessionList.conflate().first().firstOrNull { it.sessionType == SessionType.CHAT }
        }
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
        chatCompletions(data, chatID)
    }

    private suspend fun chatCompletions(data: String, chatID: Long) {
        runCatching {
            sseService.chat(
                listOf(Message(Role.USER.value, data))
            ).collect {
                updateAnswer(it, chatID)
            }
        }.onFailure {
            _answer.emit(ChatResult.Error(it))
        }
    }

    fun changeSession(session: Session?) {
        _currentSession.value = session
    }

    private suspend fun updateAnswer(data: ChatResult<ChatResponse>, chatID: Long) {
        if (data is ChatResult.Finish) {
            (_answer.value as? ChatResult.Progress)?.let {
                chatHistoryStore.updateReceiveHistory(
                    chatID,
                    it.data.toChatContent(),
                    it.data.reasoningContent,
                )
            }
        }

        data.toChatContentResult()?.let { cur ->
            if (cur is ChatResult.Progress && _answer.value is ChatResult.Progress) {
                (_answer.value as ChatResult.Progress).data.let {
                    _answer.emit(ChatResult.Progress(it.appendContent(cur.data)))
                }
            } else if (cur is ChatResult.Finish) {
                // mark down parse need time, so move to parse finish to emit finish, otherwise it makes UI flicker
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

    suspend fun deleteChatHistory(chatHistory: ChatHistory): Boolean {
        return chatHistoryStore.deleteChatHistory(chatHistory)
    }
}
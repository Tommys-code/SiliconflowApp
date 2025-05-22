package com.tommy.siliconflow.app.datasbase

import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.extensions.sendHistory
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface ChatHistoryStore {
    fun getSessionList(useID: String): Flow<List<Session>>
    fun getChatHistory(sessionID: Long): Flow<List<ChatHistory>>

    suspend fun updateSession(session: Session): Boolean
    suspend fun deleteSession(session: List<Session>): Boolean
    suspend fun createSession(userID: String, title: String): Pair<Session, Long>
    suspend fun insertSendHistory(sessionID: Long, content: String): Long
    suspend fun updateReceiveHistory(chatID: Long, content: ChatContent, thinking: String? = null)
    suspend fun deleteChatHistory(chatHistory: ChatHistory): Boolean
}

@OptIn(ExperimentalTime::class)
class ChatHistoryStoreImpl(private val appDatabase: AppDatabase) : ChatHistoryStore {

    override fun getSessionList(useID: String): Flow<List<Session>> {
        return appDatabase.sessionDao().querySessions(useID)
    }

    override fun getChatHistory(sessionID: Long): Flow<List<ChatHistory>> {
        return appDatabase.chatHistoryDao().getChatByID(sessionID)
    }

    override suspend fun createSession(userID: String, title: String): Pair<Session, Long> {
        val time = Clock.System.now().toEpochMilliseconds()
        val session =
            appDatabase.sessionDao().insertAndGet(
                Session(title = title, updateTime = time, userID = userID)
            )
        val chatID = insertHistory(ChatContent(title, Role.USER).sendHistory(session.id, time))
        return session to chatID
    }

    override suspend fun insertSendHistory(sessionID: Long, content: String): Long {
        val time = Clock.System.now().toEpochMilliseconds()
        val id =
            appDatabase.chatHistoryDao()
                .insert(ChatContent(content, Role.USER).sendHistory(sessionID, time))
        appDatabase.sessionDao().updateTime(sessionID, time)
        return id
    }

    override suspend fun updateReceiveHistory(
        chatID: Long,
        content: ChatContent,
        thinking: String?
    ) {
        appDatabase.chatHistoryDao()
            .updateReceive(chatID, content.content, content.role, thinking)
    }

    override suspend fun updateSession(session: Session): Boolean {
        return appDatabase.sessionDao().updateSession(session) > 0
    }

    private suspend fun insertHistory(history: ChatHistory): Long {
        return appDatabase.chatHistoryDao().insert(history)
    }

    override suspend fun deleteSession(session: List<Session>): Boolean {
        return appDatabase.sessionDao().deleteSession(session) > 0
    }

    override suspend fun deleteChatHistory(chatHistory: ChatHistory): Boolean {
        val success = appDatabase.chatHistoryDao().deleteChatHistory(chatHistory) > 0
        if (appDatabase.chatHistoryDao().checkSessionChatExists(chatHistory.sessionId) == 0) {
            appDatabase.sessionDao().deleteById(chatHistory.sessionId)
        }
        return success
    }
}
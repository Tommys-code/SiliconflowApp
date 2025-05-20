package com.tommy.siliconflow.app.datasbase

import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.extensions.receiveHistory
import com.tommy.siliconflow.app.extensions.sendHistory
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface ChatHistoryStore {
    fun getSessionList(): Flow<List<Session>>
    fun getChatHistory(sessionID: Long): Flow<List<ChatHistory>>

    suspend fun updateSession(session: Session): Boolean
    suspend fun createSession(title: String): Session
    suspend fun insertSendHistory(sessionID: Long, content: String)
    suspend fun insertReceiveHistory(sessionID: Long, content: ChatContent)
}

@OptIn(ExperimentalTime::class)
class ChatHistoryStoreImpl(private val appDatabase: AppDatabase) : ChatHistoryStore {

    override fun getSessionList(): Flow<List<Session>> {
        return appDatabase.sessionDao().querySessions()
    }

    override fun getChatHistory(sessionID: Long): Flow<List<ChatHistory>> {
        return appDatabase.chatHistoryDao().getChatByID(sessionID)
    }

    override suspend fun createSession(title: String): Session {
        val time = Clock.System.now().toEpochMilliseconds()
        val session = appDatabase.sessionDao().insertAndGet(Session(title = title, updateTime = time))
        insertHistory(ChatContent(title, Role.USER).sendHistory(session.id, time))
        return session
    }

    override suspend fun insertSendHistory(sessionID: Long, content: String) {
        val time = Clock.System.now().toEpochMilliseconds()
        appDatabase.chatHistoryDao().insert(ChatContent(content, Role.USER).sendHistory(sessionID, time))
        appDatabase.sessionDao().updateTime(sessionID, time)
    }

    override suspend fun insertReceiveHistory(sessionID: Long, content: ChatContent) {
        appDatabase.chatHistoryDao()
            .insert(content.receiveHistory(sessionID, Clock.System.now().toEpochMilliseconds()))
    }

    override suspend fun updateSession(session: Session): Boolean {
        return appDatabase.sessionDao().updateSession(session) > 0
    }

    private suspend fun insertHistory(history: ChatHistory) {
        appDatabase.chatHistoryDao().insert(history)
    }
}
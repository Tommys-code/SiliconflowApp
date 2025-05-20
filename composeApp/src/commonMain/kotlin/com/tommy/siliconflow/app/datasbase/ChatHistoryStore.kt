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
    fun getSessionList(): Flow<List<Session>>
    fun getChatHistory(sessionID: Long): Flow<List<ChatHistory>>

    suspend fun createSession(title: String): Long
    suspend fun insertSendHistory(sessionID: Long, content: String)
}

@OptIn(ExperimentalTime::class)
class ChatHistoryStoreImpl(private val appDatabase: AppDatabase) : ChatHistoryStore {

    override fun getSessionList(): Flow<List<Session>> {
        return appDatabase.sessionDao().querySessions()
    }

    override fun getChatHistory(sessionID: Long): Flow<List<ChatHistory>> {
        return appDatabase.chatHistoryDao().getChatByID(sessionID)
    }

    override suspend fun createSession(title: String): Long {
        val time = Clock.System.now().toEpochMilliseconds()
        val id = appDatabase.sessionDao().insert(Session(title = title, updateTime = time))
        insertHistory(ChatContent(title, Role.USER).sendHistory(id, time))
        return id
    }

    override suspend fun insertSendHistory(sessionID: Long, content: String) {
        appDatabase.chatHistoryDao().insert(
            ChatContent(content, Role.USER).sendHistory(sessionID, Clock.System.now().toEpochMilliseconds())
        )
    }

    private suspend fun insertHistory(history: ChatHistory) {
        appDatabase.chatHistoryDao().insert(history)
    }
}
package com.tommy.siliconflow.app.datasbase

import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.data.db.SessionType
import com.tommy.siliconflow.app.data.network.ImageGenerationsResponse
import com.tommy.siliconflow.app.extensions.toImageCreationHistory
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface ImageCreationStore {
    suspend fun getSession(sessionID: Long): Session
    fun getHistory(sessionID: Long): Flow<List<ImageCreationHistory>>

    suspend fun createSession(userID: String, title: String): Session
    suspend fun insertHistory(sessionID: Long, imageCreationData: ImageCreationData): Long
    suspend fun updateHistory(id: Long, imageData: ImageGenerationsResponse)

    suspend fun deleteChatHistory(history: ImageCreationHistory): Boolean
}

class ImageCreationStoreImpl(private val appDatabase: AppDatabase) : ImageCreationStore {

    override suspend fun getSession(sessionID: Long): Session {
        return appDatabase.sessionDao().getById(sessionID)
    }

    override fun getHistory(sessionID: Long): Flow<List<ImageCreationHistory>> {
        return appDatabase.imageHistoryDao().getChatBySessionId(sessionID)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun createSession(userID: String, title: String): Session {
        val time = Clock.System.now().toEpochMilliseconds()
        val session = appDatabase.sessionDao().insertAndGet(
            Session(
                title = title,
                updateTime = time,
                userID = userID,
                sessionType = SessionType.IMAGE
            )
        )
        return session
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun insertHistory(
        sessionID: Long,
        imageCreationData: ImageCreationData
    ): Long {
        val time = Clock.System.now().toEpochMilliseconds()
        appDatabase.sessionDao().updateTime(sessionID, time)
        return appDatabase.imageHistoryDao()
            .insert(imageCreationData.toImageCreationHistory(sessionID, time))
    }

    override suspend fun updateHistory(id: Long, imageData: ImageGenerationsResponse) {
        appDatabase.imageHistoryDao().update(id, imageData.images.map { it.url }, imageData.seed)
    }

    override suspend fun deleteChatHistory(history: ImageCreationHistory): Boolean {
        val success = appDatabase.imageHistoryDao().deleteHistory(history) > 0
        if (appDatabase.imageHistoryDao().checkSessionChatExists(history.sessionId) == 0) {
            appDatabase.sessionDao().deleteById(history.sessionId)
        }
        return success
    }
}
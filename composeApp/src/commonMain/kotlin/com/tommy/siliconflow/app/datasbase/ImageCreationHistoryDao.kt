package com.tommy.siliconflow.app.datasbase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageCreationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ImageCreationHistory): Long

    @Query("SELECT * FROM ImageCreationHistory WHERE sessionId = :sessionId ORDER BY createTime DESC")
    fun getChatBySessionId(sessionId: Long): Flow<List<ImageCreationHistory>>

    @Query("UPDATE ImageCreationHistory SET urls = :images,seed = :seed WHERE id = :id")
    suspend fun update(
        id: Long,
        images: List<String>,
        seed: Int
    )

    @Delete
    suspend fun deleteHistory(chatHistory: ImageCreationHistory): Int

    @Query("SELECT COUNT(*) FROM ImageCreationHistory WHERE sessionId = :sessionId")
    suspend fun checkSessionChatExists(sessionId: Long): Int
}
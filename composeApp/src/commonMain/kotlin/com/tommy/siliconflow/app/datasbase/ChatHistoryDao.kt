package com.tommy.siliconflow.app.datasbase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ChatHistory): Long

    @Update
    suspend fun updateHistory(history: ChatHistory)

    @Query("SELECT * FROM ChatHistory WHERE sessionId = :sessionId ORDER BY createTime DESC")
    fun getChatByID(sessionId: Long): Flow<List<ChatHistory>>

    @Query("UPDATE ChatHistory SET receive_content = :newContent,receive_role = :newRole,thinking = :thinking WHERE id = :id")
    suspend fun updateReceive(
        id: Long,
        newContent: String?,
        newRole: Role,
        thinking: String? = null
    )

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistory): Int

    @Query("SELECT COUNT(*) FROM ChatHistory WHERE sessionId = :sessionId")
    suspend fun checkSessionChatExists(sessionId: Long): Int
}
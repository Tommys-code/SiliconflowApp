package com.tommy.siliconflow.app.datasbase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tommy.siliconflow.app.data.db.ChatHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ChatHistory): Long

    @Update
    suspend fun updateHistory(history: ChatHistory)

    @Query("SELECT * FROM ChatHistory WHERE sessionId = :sessionId")
    fun getChatByID(sessionId: Long): Flow<List<ChatHistory>>
}
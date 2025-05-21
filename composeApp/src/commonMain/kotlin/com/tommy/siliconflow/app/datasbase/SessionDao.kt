package com.tommy.siliconflow.app.datasbase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tommy.siliconflow.app.data.db.Session
import kotlinx.coroutines.flow.Flow


@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: Session): Long

    @Transaction
    suspend fun insertAndGet(session: Session): Session {
        val id = insert(session)
        return getById(id)
    }

    @Update
    suspend fun updateSession(session: Session): Int

    @Delete
    suspend fun deleteSession(session: List<Session>): Int

    @Query("UPDATE Session SET updateTime = :newTime WHERE id = :sessionId")
    suspend fun updateTime(sessionId: Long, newTime: Long)

    @Query("SELECT * FROM Session WHERE userID = :userID OR userId = '' ORDER BY updateTime DESC")
    fun querySessions(userID: String): Flow<List<Session>>

    @Query("SELECT * FROM Session WHERE id = :id")
    suspend fun getById(id: Long): Session

}

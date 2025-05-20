package com.tommy.siliconflow.app.datasbase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tommy.siliconflow.app.data.db.Session
import kotlinx.coroutines.flow.Flow


@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Query("SELECT * FROM Session")
    fun querySessions(): Flow<List<Session>>

}

package com.tommy.siliconflow.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tommy.siliconflow.app.ui.dialog.ChatType
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val updateTime: Long,
    @ColumnInfo(defaultValue = "")
    val userID: String,
    @ColumnInfo(defaultValue = "CHAT")
    val sessionType: SessionType = SessionType.CHAT,
)

enum class SessionType {
    CHAT,
    IMAGE;
}

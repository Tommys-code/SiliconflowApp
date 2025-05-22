package com.tommy.siliconflow.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("sessionId")],
)
data class ChatHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    @Embedded(prefix = "send_") val send: ChatContent? = null,
    @Embedded(prefix = "receive_") val receive: ChatContent? = null,
    val createTime: Long,
    val thinking: String? = null,
)

@Serializable
@Entity
data class ChatContent(
    val content: String,
    val role: Role,
)

enum class Role(val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    UNKNOWN("unknown");

    companion object {
        fun valueOfIgnoreCase(name: String?): Role {
            return enumValues<Role>().firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: UNKNOWN
        }
    }
}

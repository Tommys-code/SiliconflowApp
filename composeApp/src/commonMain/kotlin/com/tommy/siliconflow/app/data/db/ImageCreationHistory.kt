package com.tommy.siliconflow.app.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tommy.siliconflow.app.data.ImageRatio
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
data class ImageCreationHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val createTime: Long,
    val prompt: String,
    val ratio: String = ImageRatio.RATIO_1_1.value,
    val batchSize: Int = 1,
    val baseImage: String? = null,
    @Embedded
    val image: ImageData? = null,
)

@Serializable
@Entity
data class ImageData(
    val urls: List<String>,
    val seed: Int,
)


package com.tommy.siliconflow.app.model

import com.tommy.siliconflow.app.data.DoubleToIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextAIModel(
    val model: String,
    @SerialName("max_tokens")
    @Serializable(with = DoubleToIntSerializer::class)
    val maxTokens: Int,
    val manu: String,
    val desc: String,
)

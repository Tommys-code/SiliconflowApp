package com.tommy.siliconflow.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextAIModel(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val manu: String,
    val desc: String,
)

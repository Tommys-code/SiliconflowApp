package com.tommy.siliconflow.app.model

sealed class LocalAIModel(
    open val model: String,
    open val manu: String?,
    open val desc: String?,
    open val disabled: Boolean = false,
)

data class LocalAITextModel(
    override val model: String,
    override val manu: String,
    override val desc: String,
    override val disabled: Boolean,
    val maxTokens: Int,
    val type: TextAIType,
) : LocalAIModel(model, manu, desc)

enum class TextAIType {
    LLM,
    VLM;
}

fun TextAIModel.toLocalAIModel(): LocalAITextModel {
    return LocalAITextModel(
        model = model,
        manu = manu,
        desc = desc,
        maxTokens = maxTokens,
        disabled = disabled ?: false,
        type = TextAIType.valueOf(type),
    )
}
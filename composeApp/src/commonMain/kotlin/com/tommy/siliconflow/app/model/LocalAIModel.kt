package com.tommy.siliconflow.app.model

sealed class LocalAIModel(
    open val model: String,
    open val manu: String?,
    open val desc: String?,
)

data class LocalAITextModel(
    override val model: String,
    override val manu: String,
    override val desc: String,
    val maxTokens: Int,
) : LocalAIModel(model, manu, desc)

fun TextAIModel.toLocalAIModel(): LocalAIModel {
    return LocalAITextModel(
        model = model,
        manu = manu,
        desc = desc,
        maxTokens = maxTokens,
    )
}
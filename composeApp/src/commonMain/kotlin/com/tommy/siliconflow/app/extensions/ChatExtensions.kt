package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.network.ChoiceDelta
import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role

fun ChatContent.sendHistory(sessionID: Long, time: Long) = ChatHistory(
    sessionId = sessionID,
    send = this,
    createTime = time,
)

fun ChatContent.receiveHistory(sessionID: Long, time: Long) = ChatHistory(
    sessionId = sessionID,
    receive = this,
    createTime = time,
)

fun ChatResponse.getChoiceDelta() = this.choices.getOrNull(0)?.delta

fun ChoiceDelta.toChatContent() = ChatContent(
    content.orEmpty(),
    Role.valueOfIgnoreCase(role),
)

fun ChatResult<ChatResponse>.toChatContentResult(): ChatResult<ChoiceDelta>? = when (this) {
    ChatResult.Start -> ChatResult.Start
    ChatResult.Finish -> ChatResult.Finish
    is ChatResult.Progress -> data.getChoiceDelta()?.let { ChatResult.Progress(it) }
    is ChatResult.Error -> ChatResult.Error(this.e)
}

fun ChoiceDelta.appendContent(choiceDelta: ChoiceDelta): ChoiceDelta {
    return copy(
        content = content.appendContent(choiceDelta.content),
        reasoningContent = reasoningContent.appendContent(choiceDelta.reasoningContent),
    )
}

private fun String?.appendContent(content: String?): String? {
    if (this == null) return content
    return content?.let { this + it } ?: this
}
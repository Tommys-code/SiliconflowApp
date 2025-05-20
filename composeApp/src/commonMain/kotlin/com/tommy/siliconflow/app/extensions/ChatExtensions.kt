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

fun ChoiceDelta.getContent() = content ?: reasoningContent.orEmpty()

fun ChatResponse.toChatContent() = getChoiceDelta()?.let {
    ChatContent(it.getContent(), Role.valueOfIgnoreCase(it.role))
}

fun ChatResult<ChatResponse>.toChatContentResult(): ChatResult<ChatContent>? = when (this) {
    ChatResult.Start -> ChatResult.Start
    ChatResult.Finish -> ChatResult.Finish
    is ChatResult.Progress -> this.data.toChatContent()?.let { ChatResult.Progress(it) }
}
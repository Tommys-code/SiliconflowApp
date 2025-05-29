package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.data.AnswerMarkdown
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.MarkdownChatHistory
import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Role
import com.tommy.siliconflow.app.data.db.SessionType
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.data.network.ChoiceDelta
import com.tommy.siliconflow.app.ui.components.parseMarkdown
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_chat
import siliconflowapp.composeapp.generated.resources.ic_image

fun ChatContent.sendHistory(sessionID: Long, time: Long) = ChatHistory(
    sessionId = sessionID,
    send = this,
    createTime = time,
)

fun ChatResponse.getChoiceDelta() = this.choices.getOrNull(0)?.delta

fun ChoiceDelta.toChatContent() = ChatContent(
    content.orEmpty(),
    Role.valueOfIgnoreCase(role),
)

fun ChatResult<ChatResponse>.toChatContentResult(): ChatResult<ChoiceDelta>? = toOtherResult {
    it.getChoiceDelta()
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

fun <IN, OUT> ChatResult<IN>.toOtherResult(transform: (IN) -> OUT?): ChatResult<OUT>? {
    return when (this) {
        is ChatResult.Start -> ChatResult.Start
        is ChatResult.Progress -> transform(this.data)?.let { ChatResult.Progress(it) }
        is ChatResult.Finish -> ChatResult.Finish
        is ChatResult.Error -> ChatResult.Error(this.e)
    }
}

suspend fun ChatHistory.toMarkdownChatHistory(): MarkdownChatHistory {
    return MarkdownChatHistory(
        chatHistory = this,
        contentMarkdown = receive?.content?.let {
            parseMarkdown(content = it)
        },
        thinkingMarkdown = thinking?.let {
            parseMarkdown(content = it)
        },
    )
}

suspend fun <IN, OUT> ChatResult<IN>.toOtherResultBlock(transform: suspend (IN) -> OUT): ChatResult<OUT>? {
    return when (this) {
        is ChatResult.Start -> ChatResult.Start
        is ChatResult.Progress -> transform(this.data)?.let { ChatResult.Progress(it) }
        is ChatResult.Finish -> ChatResult.Finish
        is ChatResult.Error -> ChatResult.Error(this.e)
    }
}

suspend fun ChatResult<ChoiceDelta>.toAnswerMarkdown(): ChatResult<AnswerMarkdown>? {
    return toOtherResultBlock {
        AnswerMarkdown(
            contentMarkdown = it.content?.let { content ->
                parseMarkdown(content = content)
            },
            reasoningMarkdown = it.reasoningContent?.let { content ->
                parseMarkdown(content = content)
            },
        )
    }
}

fun SessionType.getSessionIcon() = when (this) {
    SessionType.CHAT -> Res.drawable.ic_chat
    SessionType.IMAGE -> Res.drawable.ic_image
}

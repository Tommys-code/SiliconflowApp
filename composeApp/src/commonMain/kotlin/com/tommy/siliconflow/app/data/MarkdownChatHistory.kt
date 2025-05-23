package com.tommy.siliconflow.app.data

import com.mikepenz.markdown.model.State
import com.tommy.siliconflow.app.data.db.ChatHistory

data class MarkdownChatHistory(
    val chatHistory: ChatHistory,
    val contentMarkdown: State?,
    val thinkingMarkdown: State?,
)

data class AnswerMarkdown(
    val contentMarkdown: State?,
    val reasoningMarkdown: State?,
)

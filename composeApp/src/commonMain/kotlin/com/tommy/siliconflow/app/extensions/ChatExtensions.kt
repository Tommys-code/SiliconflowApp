package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.data.db.ChatContent
import com.tommy.siliconflow.app.data.db.ChatHistory

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
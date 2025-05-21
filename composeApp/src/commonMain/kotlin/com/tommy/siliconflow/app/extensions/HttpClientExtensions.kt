package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.network.ChatResponse
import com.tommy.siliconflow.app.network.JsonSerializationHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.serverSentEventsSession
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

const val SSE_DONE = "[DONE]"

suspend inline fun <reified T> HttpClient.sseChat(
    urlString: String,
    body: T
): Flow<ChatResult<ChatResponse>> {
    return serverSentEventsSession(
        block = {
            url.takeFrom(urlString)
            method = HttpMethod.Post
            setBody(body)
        }
    ).incoming.mapNotNull {
        if (it.data != SSE_DONE) {
            try {
                val data = JsonSerializationHelper.jsonX()
                    .decodeFromString<ChatResponse>(it.data.orEmpty())
                ChatResult.Progress(data)
            } catch (e: Exception) {
                ChatResult.Error(e)
            }
        } else {
            ChatResult.Finish
        }
    }
}

suspend inline fun <reified T> HttpClient.sse(
    urlString: String,
    body: T,
    crossinline block: suspend ClientSSESession.() -> Unit,
) {
    sse(urlString = urlString, request = {
        method = HttpMethod.Post
        setBody(body)
    }) {
        incoming.mapNotNull { it.data }
        block.invoke(this)
    }
}
package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.data.ChatResponse
import com.tommy.siliconflow.app.network.JsonSerializationHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.serverSentEventsSession
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

const val SSE_DONE = "[DONE]"

suspend inline fun <reified T> HttpClient.sseChat(
    urlString: String,
    body: T
): Flow<ChatResponse> {
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
                data
            } catch (e: Exception) {
                throw JsonConvertException("Failed to decode: ${e.message}")
            }
        } else {
            null
        }
    }
}
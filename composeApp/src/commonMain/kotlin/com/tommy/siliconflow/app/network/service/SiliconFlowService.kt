package com.tommy.siliconflow.app.network.service

import com.tommy.siliconflow.app.data.network.ImageGenerationsRequest
import com.tommy.siliconflow.app.data.network.ImageGenerationsResponse
import com.tommy.siliconflow.app.data.network.UserInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

private const val IMAGE_GENERATION_RETRY = 5

private const val USER_INFO_URL = "user/info"
private const val IMAGE_GENERATIONS_URL = "image/generations"

class SiliconFlowService(private val unAuthClient: HttpClient, private val authClient: HttpClient) {
    suspend fun getUserInfo(apiKey: String): UserInfoResponse {
        return unAuthClient.get(USER_INFO_URL) {
            bearerAuth(apiKey)
        }.body<UserInfoResponse>()
    }

    suspend fun imageGenerations(request: ImageGenerationsRequest): ImageGenerationsResponse {
        return authClient.post(IMAGE_GENERATIONS_URL) {
            setBody(request)
            timeout {
                socketTimeoutMillis = Long.MAX_VALUE
            }
            retry { this.maxRetries = IMAGE_GENERATION_RETRY }
        }.body<ImageGenerationsResponse>()
    }
}
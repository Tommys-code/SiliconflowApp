package com.tommy.siliconflow.app.network.service

import com.tommy.siliconflow.app.data.network.UserInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get

private const val USER_INFO_URL = "user/info"

class SiliconFlowService(private val unAuthClient: HttpClient, private val authClient: HttpClient) {
    suspend fun getUserInfo(apiKey: String): UserInfoResponse {
        return unAuthClient.get(USER_INFO_URL) {
            bearerAuth(apiKey)
        }.body<UserInfoResponse>()
    }

//    suspend fun getUserInfo(): UserInfoResponse {
//        return client.get(USER_INFO_URL).body<UserInfoResponse>()
//    }
}
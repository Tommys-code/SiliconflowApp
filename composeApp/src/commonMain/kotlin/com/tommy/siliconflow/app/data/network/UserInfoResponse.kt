package com.tommy.siliconflow.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean,
    @SerialName("data")
    val data: UserInfo?,
)

@Serializable
data class UserInfo(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("image")
    val image: String,
    @SerialName("email")
    val email: String,
    @SerialName("balance")
    val balance: Float,
    @SerialName("chargeBalance")
    val chargeBalance: Float,
    @SerialName("totalBalance")
    val totalBalance: Float,
)
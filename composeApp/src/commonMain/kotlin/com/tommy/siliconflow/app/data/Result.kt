package com.tommy.siliconflow.app.data

import com.tommy.siliconflow.app.data.network.UserInfo


sealed class Resource<T>(val result: T?) {
    data class Loading<T>(val loading: Boolean = false) : Resource<T>(null)
    data class Success<T>(val data: T) : Resource<T>(data)
    data class Error<T>(val exception: Throwable) : Resource<T>(null)

    companion object {
        val loading = Loading<UserInfo>(true)
    }
}

sealed class ChatResult<out T> {
    data object Start : ChatResult<Nothing>()
    data class Progress<T>(val data: T) : ChatResult<T>()
    data object Finish : ChatResult<Nothing>()
}
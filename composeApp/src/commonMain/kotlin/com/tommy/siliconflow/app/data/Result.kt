package com.tommy.siliconflow.app.data


sealed class Resource<out T>(val result: T?) {
    data class Loading(val loading: Boolean = false) : Resource<Nothing>(null)
    data class Success<T>(val data: T) : Resource<T>(data)
    data class Error(val exception: Throwable) : Resource<Nothing>(null)

    companion object {
        val loading = Loading(true)
        val init = Loading(false)
    }
}

sealed class ChatResult<out T> {
    data object Start : ChatResult<Nothing>()
    data class Progress<T>(val data: T) : ChatResult<T>()
    data object Finish : ChatResult<Nothing>()
    data class Error(val e: Throwable) : ChatResult<Nothing>()
}
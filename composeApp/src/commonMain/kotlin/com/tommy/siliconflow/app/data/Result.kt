package com.tommy.siliconflow.app.data


sealed class Resource<T>(val result: T?) {
    data class Loading<T>(val loading: Boolean = false) : Resource<T>(null)
    data class Success<T>(val data: T) : Resource<T>(data)
    data class Error<T>(val exception: Throwable) : Resource<T>(null)

    companion object{
        val loading = Loading<UserInfo>(true)
    }
}
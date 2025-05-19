package com.tommy.siliconflow.app.network.error

data class AuthException(override val message: String) : GeneralException(message)
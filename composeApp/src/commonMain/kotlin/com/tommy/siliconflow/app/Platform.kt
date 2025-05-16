package com.tommy.siliconflow.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
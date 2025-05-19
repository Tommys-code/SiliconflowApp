package com.tommy.siliconflow.app.network

import kotlinx.serialization.json.Json

class JsonSerializationHelper {
    companion object {
        fun jsonX(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                encodeDefaults = true
                explicitNulls = false
            }
        }
    }
}
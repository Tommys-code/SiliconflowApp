package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable
import coil3.Image

expect fun Image.toByteArray(): ByteArray?

expect class LocalImageProcessing {
    fun imageToByteArray(image: Image): ByteArray?
    suspend fun saveToLocal(data: ByteArray, name: String): String?
    fun getReferenceImageUri(fileName: String): String
}

@Composable
expect fun rememberLocalImageProcessing(): LocalImageProcessing

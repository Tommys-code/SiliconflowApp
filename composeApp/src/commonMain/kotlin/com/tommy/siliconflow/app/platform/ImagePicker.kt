package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable

expect class ImagePicker {
    fun launchPicker()
}

@Composable
expect fun rememberImagerPicker(onResult: (ImageData) -> Unit): ImagePicker

data class ImageData(
    val uri: String,
    val mimeType: String?
)
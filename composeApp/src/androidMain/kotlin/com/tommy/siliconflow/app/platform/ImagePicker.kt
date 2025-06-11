package com.tommy.siliconflow.app.platform

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.compose.LocalPlatformContext

actual class ImagePicker(private val launcher: ManagedActivityResultLauncher<String, Uri?>) {
    actual fun launchPicker() {
        launcher.launch("image/*")
    }
}

@Composable
actual fun rememberImagerPicker(onResult: (ImageData) -> Unit): ImagePicker {
    val context = LocalPlatformContext.current
    val launch =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                onResult(
                    ImageData(
                        uri = it.toString(),
                        mimeType = context.contentResolver.getType(it)
                    )
                )
            }
        }
    val imagePicker = remember { ImagePicker(launch) }
    return imagePicker
}

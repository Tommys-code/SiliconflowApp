package com.tommy.siliconflow.app.platform

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.Image
import coil3.toBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import androidx.core.net.toUri

actual fun Image.toByteArray(): ByteArray? {
    val outputStream = ByteArrayOutputStream()
    val format = android.graphics.Bitmap.CompressFormat.JPEG
    outputStream.use {
        toBitmap().compress(format, 100, it)
    }
    return outputStream.toByteArray()
}

actual class LocalImageProcessing(private val context: Context) {

    private val cacheDir by lazy { context.cacheDir }

    actual fun imageToByteArray(image: Image): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        val format = android.graphics.Bitmap.CompressFormat.JPEG
        outputStream.use {
            image.toBitmap().compress(format, 100, it)
        }
        return outputStream.toByteArray()
    }

    actual suspend fun saveToLocal(data: ByteArray, name: String): String? {
        return runCatching {
            val dir = File(cacheDir, REFERENCE_IMAGE_DIR)
            if (!dir.exists() && !dir.mkdirs()) return null
            val fileName = name.md5()
            val file = File(dir, fileName)
            if (file.exists()) return fileName
            file.outputStream().use { it.write(data) }
            fileName
        }.getOrNull()
    }

    actual fun getReferenceImageUri(fileName: String): String {
        return "$cacheDir/$REFERENCE_IMAGE_DIR/$fileName".toUri().toString()
    }

    private fun String.md5(): String {
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(this.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val REFERENCE_IMAGE_DIR = "reference_image_cache"
    }
}

@Composable
actual fun rememberLocalImageProcessing(): LocalImageProcessing {
    val context = LocalContext.current
    val imageGenerate = remember { LocalImageProcessing(context) }
    return imageGenerate
}
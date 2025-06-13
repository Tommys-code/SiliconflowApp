package com.tommy.siliconflow.app.platform

import coil3.Image
import coil3.PlatformContext
import coil3.toBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import androidx.core.net.toUri
import com.tommy.siliconflow.app.ui.components.REFERENCE_IMAGE_DIR

actual fun Image.toByteArray(): ByteArray? {
    val outputStream = ByteArrayOutputStream()
    val format = android.graphics.Bitmap.CompressFormat.JPEG
    outputStream.use {
        toBitmap().compress(format, 100, it)
    }
    return outputStream.toByteArray()
}

actual fun saveToLocal(context: PlatformContext, data: ByteArray, name: String): String? {
    return runCatching {
        val dir = File(context.cacheDir, REFERENCE_IMAGE_DIR)
        if (!dir.exists() && !dir.mkdirs()) return null
        val fileName = name.md5()
        val file = File(dir, fileName)
        if (file.exists()) return file.absolutePath
        file.outputStream().use { it.write(data) }
        fileName
    }.getOrNull()
}

actual fun PlatformContext.getReferenceImageUri(fileName: String): String {
    return "$cacheDir/$REFERENCE_IMAGE_DIR/$fileName".toUri().toString()
}

private fun String.md5(): String {
    val digest = MessageDigest.getInstance("MD5")
    val hashBytes = digest.digest(this.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}
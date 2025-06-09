package com.tommy.siliconflow.app.platform

import coil3.Image
import coil3.toBitmap
import java.io.ByteArrayOutputStream

actual fun Image.toByteArray(): ByteArray? {
    val outputStream = ByteArrayOutputStream()
    val format = android.graphics.Bitmap.CompressFormat.JPEG
    toBitmap().compress(format, 100, outputStream)
    return outputStream.toByteArray()
}
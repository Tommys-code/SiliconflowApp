package com.tommy.siliconflow.app.platform

import coil3.Image
import coil3.toBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image as SkiaImage

actual fun Image.toByteArray(): ByteArray? {
    return SkiaImage.makeFromBitmap(this.toBitmap()).encodeToData(EncodedImageFormat.JPEG)?.bytes
}
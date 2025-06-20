package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.Image
import coil3.toBitmap
import kotlinx.cinterop.*
import org.jetbrains.skia.EncodedImageFormat
import platform.Foundation.*
import platform.CoreCrypto.*
import org.jetbrains.skia.Image as SkiaImage

actual fun Image.toByteArray(): ByteArray? {
    return SkiaImage.makeFromBitmap(this.toBitmap()).encodeToData(EncodedImageFormat.JPEG)?.bytes
}

@OptIn(ExperimentalForeignApi::class)
actual class LocalImageProcessing {

    actual fun imageToByteArray(image: Image): ByteArray? {
        return SkiaImage.makeFromBitmap(image.toBitmap()).encodeToData(EncodedImageFormat.JPEG)?.bytes
    }

    actual suspend fun saveToLocal(data: ByteArray, name: String): String? {
        val cachesDir = getCachesDirectory()
        val cacheFolder = "${cachesDir}/$REFERENCE_IMAGE_DIR"
        val fileManager = NSFileManager.defaultManager

        if (!fileManager.fileExistsAtPath(cacheFolder)) {
            fileManager.createDirectoryAtPath(
                cacheFolder,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
        }

        val fileName = sha256(name)
        val filePath = "$cacheFolder/${fileName}"
        if (fileManager.fileExistsAtPath(filePath)) {
            return fileName
        }
        return if (saveBytesToFile(data, filePath)) {
            fileName
        } else {
            null
        }
    }

    actual fun getReferenceImageUri(fileName: String): String {
        val cachesDir = getCachesDirectory()
        val filePath = "${cachesDir}/$REFERENCE_IMAGE_DIR/$fileName"
        return NSURL.fileURLWithPath(filePath).absoluteString.orEmpty()
    }

    private fun getCachesDirectory(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(
            NSCachesDirectory,
            NSUserDomainMask
        )
        val nsUrl = urls.first() as NSURL
        return nsUrl.path!!
    }

    private fun sha256(input: String): String = memScoped {
        val cstr = input.cstr.getPointer(this)
        val buffer = allocArray<UByteVar>(CC_SHA256_DIGEST_LENGTH)
        CC_SHA256(cstr, input.length.convert(), buffer)
        buildString {
            for (i in 0 until CC_SHA256_DIGEST_LENGTH) {
                append(buffer[i].toInt().and(0xff).toString(16).padStart(2, '0'))
            }
        }
    }

    private fun saveBytesToFile(data: ByteArray, filePath: String): Boolean = memScoped {
        val nsData = data.toNSData()
        val url = NSURL.fileURLWithPath(filePath)
        return nsData.writeToURL(url, atomically = true)
    }

    @OptIn(BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }

    companion object {
        private const val REFERENCE_IMAGE_DIR = "reference_image_cache"
    }
}

@Composable
actual fun rememberLocalImageProcessing(): LocalImageProcessing {
    val imageGenerate = remember { LocalImageProcessing() }
    return imageGenerate
}

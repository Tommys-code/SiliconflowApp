package com.tommy.siliconflow.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.maxBitmapSize
import coil3.size.Size
import com.tommy.siliconflow.app.data.ReferenceImageInfo
import com.tommy.siliconflow.app.di.KMMInject
import com.tommy.siliconflow.app.platform.LocalImageProcessing
import com.tommy.siliconflow.app.platform.rememberLocalImageProcessing
import com.tommy.siliconflow.app.platform.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ImageProcessing(
    private val context: PlatformContext,
    private val localImageProcessing: LocalImageProcessing,
) {

    @OptIn(ExperimentalTime::class)
    suspend fun saveImage(url: String): Boolean {
        val request = ImageRequest.Builder(context).data(url).diskCacheKey(null).build()
        return (ImageLoader(context).execute(request) as? SuccessResult)?.image?.toByteArray()
            ?.let {
                KMMInject.factory.saveImage(it, "${Clock.System.now().nanosecondsOfSecond}.jpg")
            } ?: false
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getReferenceImageInfoFromUri(uri: String): ReferenceImageInfo? {
        val request = ImageRequest.Builder(context).data(uri)
            .maxBitmapSize(Size(2048, 2048)).build()
        val imageLoader = ImageLoader.Builder(context).build()
        return (imageLoader.execute(request) as? SuccessResult)?.image?.toByteArray()?.let {
            ReferenceImageInfo(
                base64Data = "data:image/jpeg;base64, ${Base64.encode(it)}",
                fileName = localImageProcessing.saveToLocal(it, uri),
            )
        }
    }

    fun getReferenceImageUri(fileName: String) = localImageProcessing.getReferenceImageUri(fileName)
}

@Composable
fun rememberImageProcessing(): ImageProcessing {
    val platformContext = LocalPlatformContext.current
    val localImageProcessing = rememberLocalImageProcessing()
    return remember { ImageProcessing(platformContext, localImageProcessing) }
}
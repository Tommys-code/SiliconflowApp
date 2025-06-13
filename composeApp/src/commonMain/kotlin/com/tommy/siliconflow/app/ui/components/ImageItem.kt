package com.tommy.siliconflow.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import coil3.request.maxBitmapSize
import coil3.size.Size
import com.tommy.siliconflow.app.data.ReferenceImageInfo
import com.tommy.siliconflow.app.di.KMMInject
import com.tommy.siliconflow.app.platform.saveToLocal
import com.tommy.siliconflow.app.platform.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

const val REFERENCE_IMAGE_DIR = "reference_image_cache"

@Composable
fun ImageItem(
    url: Any? = null,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = null,
    diskCacheKey: String? = null,
    crossfade: Boolean = true,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) = AsyncImage(
    model = ImageRequest.Builder(LocalPlatformContext.current)
        .data(url)
        .diskCacheKey(diskCacheKey)
        .crossfade(crossfade)
        .build(),
    modifier = modifier,
    placeholder = if (LocalInspectionMode.current) ColorPainter(Color.Cyan) else placeholder,
    error = error,
    contentScale = contentScale,
    alignment = alignment,
    imageLoader = SingletonImageLoader.get(LocalPlatformContext.current),
    contentDescription = contentDescription,
    colorFilter = colorFilter,
    onSuccess = onSuccess,
    onError = onError,
)

@OptIn(ExperimentalTime::class)
suspend fun saveImage(context: PlatformContext, url: String): Boolean {
    val request = ImageRequest.Builder(context).data(url).diskCacheKey(null).build()
    return (ImageLoader(context).execute(request) as? SuccessResult)?.image?.toByteArray()?.let {
        KMMInject.factory.saveImage(it, "${Clock.System.now().nanosecondsOfSecond}.jpg")
    } ?: false
}

@OptIn(ExperimentalEncodingApi::class)
suspend fun getReferenceImageInfoFromUri(
    context: PlatformContext,
    uri: String
): ReferenceImageInfo? {
    val request = ImageRequest.Builder(context).data(uri)
        .maxBitmapSize(Size(2048, 2048)).build()
    val imageLoader = ImageLoader.Builder(context).build()
    return (imageLoader.execute(request) as? SuccessResult)?.image?.toByteArray()?.let {
        ReferenceImageInfo(
            base64Data = "data:image/jpeg;base64, ${Base64.encode(it)}",
            fileName = saveToLocal(context, it, uri),
        )
    }
}

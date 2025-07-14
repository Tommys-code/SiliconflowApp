package com.tommy.siliconflow.app.data

import com.tommy.siliconflow.app.data.network.ImageContent
import com.tommy.siliconflow.app.data.network.VLMContent
import com.tommy.siliconflow.app.platform.ImageData
import com.tommy.siliconflow.app.utils.ImageProcessing

data class VLMImageData(
    val url: String? = null,
    val imageData: ImageData? = null,
    val referenceImageInfo: ReferenceImageInfo? = null,
)

fun VLMImageData.getUrl() = url ?: imageData?.uri

suspend fun List<VLMImageData>.generateReferenceImageInfo(imageProcessing: ImageProcessing): List<VLMImageData> {
    return this.mapNotNull {
        it.url?.let { data -> VLMImageData(url = data) } ?: it.imageData?.let { imageData ->
            imageProcessing.getReferenceImageInfoFromUri(imageData.uri)?.let { referenceImageInfo ->
                VLMImageData(referenceImageInfo = referenceImageInfo)
            }
        }
    }
}

fun List<VLMImageData>?.toVLMContent(): List<VLMContent> {
    return this?.mapNotNull {
        (it.url ?: it.referenceImageInfo?.base64Data)?.let { data ->
            VLMContent(
                type = "image_url",
                imageUrl = ImageContent(url = data)
            )
        }
    } ?: emptyList()
}
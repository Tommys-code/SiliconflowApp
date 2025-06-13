package com.tommy.siliconflow.app.data

import com.tommy.siliconflow.app.platform.ImageData

data class ImageCreationBaseInfo(
    val imageRadio: ImageRatio = ImageRatio.RATIO_1_1,
    val batchSize: Int = DEFAULT_BATCH_SIZE,
) {

    companion object {
        const val DEFAULT_BATCH_SIZE = 1
    }
}

data class ImageCreationDynamicData(
    val prompt: String = "",
    val referenceImage: ImageData? = null,
    val referenceImageInfo: ReferenceImageInfo? = null,
)

data class ImageCreationData(
    val dynamicData: ImageCreationDynamicData,
    val baseInfo: ImageCreationBaseInfo,
)

data class ReferenceImageInfo(
    val base64Data: String,
    val fileName: String?,
)
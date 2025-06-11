package com.tommy.siliconflow.app.data

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
)

data class ImageCreationData(
    val dynamicData: ImageCreationDynamicData,
    val baseInfo: ImageCreationBaseInfo,
)
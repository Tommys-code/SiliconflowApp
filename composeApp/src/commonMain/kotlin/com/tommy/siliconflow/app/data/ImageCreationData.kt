package com.tommy.siliconflow.app.data

data class ImageCreationData(
    val prompt: String = "",
    val imageRadio: ImageRatio = ImageRatio.RATIO_1_1,
    val batchSize: Int = DEFAULT_BATCH_SIZE,
) {

    companion object {
        const val DEFAULT_BATCH_SIZE = 1
    }
}
package com.tommy.siliconflow.app.extensions

import coil3.PlatformContext
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.ImageCreationDynamicData
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.ui.components.getReferenceImageInfoFromUri

fun ImageCreationData.toImageCreationHistory(
    sessionID: Long,
    time: Long,
) = ImageCreationHistory(
    sessionId = sessionID,
    createTime = time,
    prompt = dynamicData.prompt,
    ratio = baseInfo.imageRadio.value,
    batchSize = baseInfo.batchSize,
    baseImage = dynamicData.referenceImageInfo?.fileName,
)

suspend fun ImageCreationDynamicData.generate(context: PlatformContext) = ImageCreationDynamicData(
    prompt = this.prompt,
    referenceImage = this.referenceImage,
    referenceImageInfo = this.referenceImage?.uri?.let { getReferenceImageInfoFromUri(context, it) }
)

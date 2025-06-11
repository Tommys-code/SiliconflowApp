package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.db.ImageCreationHistory

fun ImageCreationData.toImageCreationHistory(
    sessionID: Long,
    time: Long,
) = ImageCreationHistory(
    sessionId = sessionID,
    createTime = time,
    prompt = dynamicData.prompt,
    ratio = baseInfo.imageRadio.value,
    batchSize = baseInfo.batchSize
)

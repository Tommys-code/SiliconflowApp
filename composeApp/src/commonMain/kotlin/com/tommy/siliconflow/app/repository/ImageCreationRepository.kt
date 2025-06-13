package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.ImageCreationBaseInfo
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.ImageCreationDynamicData
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.data.network.ImageGenerationsRequest
import com.tommy.siliconflow.app.data.network.ImageGenerationsResponse
import com.tommy.siliconflow.app.datasbase.ImageCreationStore
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.network.service.SiliconFlowService
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ImageCreationRepository(
    private val imageCreationStore: ImageCreationStore,
    private val service: SiliconFlowService,
    private val settingDataStore: SettingDataStore,
) {

    private val useID = settingDataStore.getUserInfo().map { it?.id }
    val imageCreationBaseInfo = settingDataStore.getImageCreationBaseInfo()

    suspend fun getSessionById(sessionID: Long) = imageCreationStore.getSession(sessionID)

    fun getHistory(sessionID: Long) = imageCreationStore.getHistory(sessionID)

    suspend fun insertHistory(sessionID: Long, data: ImageCreationDynamicData): Long {
        return imageCreationStore.insertHistory(
            sessionID, ImageCreationData(
                dynamicData = data,
                baseInfo = imageCreationBaseInfo.conflate().first(),
            )
        )
    }

    suspend fun createSession(prompt: String): Session {
        return imageCreationStore.createSession(useID.conflate().first().orEmpty(), prompt)
    }

    suspend fun updateHistory(id: Long, data: ImageGenerationsResponse) {
        imageCreationStore.updateHistory(id, data)
    }

    suspend fun createImage(data: ImageCreationDynamicData): ImageGenerationsResponse {
        val baseInfo = imageCreationBaseInfo.conflate().first()
        return service.imageGenerations(data.createImageGenerationsRequest(baseInfo))
    }

    private fun ImageCreationDynamicData.createImageGenerationsRequest(baseInfo: ImageCreationBaseInfo) =
        ImageGenerationsRequest(
            model = "Kwai-Kolors/Kolors",
            prompt = prompt,
            imageSize = baseInfo.imageRadio.value,
            batchSize = baseInfo.batchSize,
            image = referenceImageInfo?.base64Data,
        )

    suspend fun saveImageCreationData(data: ImageCreationBaseInfo) {
        settingDataStore.setImageCreationBaseInfo(data)
    }

    suspend fun deleteHistory(history: ImageCreationHistory): Boolean {
        return imageCreationStore.deleteChatHistory(history)
    }
}
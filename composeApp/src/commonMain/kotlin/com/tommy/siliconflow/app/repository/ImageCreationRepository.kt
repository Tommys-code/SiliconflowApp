package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.ImageCreationData
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
    settingDataStore: SettingDataStore,
) {

    private val useID = settingDataStore.getUserInfo().map { it?.id }

    suspend fun getSessionById(sessionID: Long) = imageCreationStore.getSession(sessionID)

    fun getHistory(sessionID: Long) = imageCreationStore.getHistory(sessionID)

    suspend fun insertHistory(sessionID: Long, data: ImageCreationData): Long {
        return imageCreationStore.insertHistory(sessionID, data)
    }

    suspend fun createSession(prompt: String): Session {
        return imageCreationStore.createSession(useID.conflate().first().orEmpty(), prompt)
    }

    suspend fun updateHistory(id: Long, data: ImageGenerationsResponse) {
        imageCreationStore.updateHistory(id, data)
    }

    suspend fun createImage(data: ImageCreationData): ImageGenerationsResponse {
        return service.imageGenerations(data.createImageGenerationsRequest())
    }

    private fun ImageCreationData.createImageGenerationsRequest() = ImageGenerationsRequest(
        model = "Kwai-Kolors/Kolors",
        prompt = prompt,
        imageSize = imageRadio.value,
        batchSize = batchSize,
    )
}
package com.tommy.siliconflow.app.data.network

import com.tommy.siliconflow.app.data.ImageRatio
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageGenerationsRequest(
    val model: String,
    val prompt: String,
    @SerialName("image_size")
    val imageSize: String = ImageRatio.RATIO_1_1.value,
    @SerialName("batch_size")
    val batchSize: Int = 1,
    @SerialName("num_inference_steps")
    val numInferenceSteps: Int = 20,
    @SerialName("guidance_scale")
    val guidanceScale: Float = 7.5f,
    @SerialName("negative_prompt")
    val negativePrompt: String? = null,
    val seed: Int? = null,
    val image: String? = null,
)
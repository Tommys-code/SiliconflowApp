package com.tommy.siliconflow.app.data.network

import kotlinx.serialization.Serializable

@Serializable
data class ImageGenerationsResponse(
    val images: List<ImageUrl>,
    val seed: Int,
)

@Serializable
data class ImageUrl(
    val url: String
)
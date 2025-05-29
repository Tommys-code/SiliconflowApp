package com.tommy.siliconflow.app.data

enum class ImageRatio(val value: String, val size: Float, val desc: String) {
    RATIO_1_1("1024x1024", 1f, "1:1");

    companion object {
        fun fromValue(value: String?): ImageRatio {
            return ImageRatio.entries.firstOrNull { it.value == value } ?: RATIO_1_1
        }
    }
}
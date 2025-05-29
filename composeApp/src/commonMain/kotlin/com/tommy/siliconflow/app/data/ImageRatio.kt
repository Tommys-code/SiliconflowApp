package com.tommy.siliconflow.app.data

enum class ImageRatio(val value: String, val size: Float, val desc: String) {
    RATIO_1_1("1024x1024", 1f, "1:1"),
    RATIO_1_2("1024x2048", 1 / 2f, "1:2"),
    RATIO_3_2("1536x1024", 3 / 2f, "3:2"),
    RATIO_3_4("1536x2048", 3 / 4f, "3:4"),
    RATIO_16_9("2048x1152", 16 / 9f, "16:9"),
    RATIO_9_16("1152x2048", 9 / 16f, "9:16")
    ;

    companion object {
        fun fromValue(value: String?): ImageRatio {
            return ImageRatio.entries.firstOrNull { it.value == value } ?: RATIO_1_1
        }
    }
}
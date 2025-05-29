package com.tommy.siliconflow.app.data

enum class ImageRatio(val value: String) {
    RATIO_1_1("1024*1024"),
    UNKNOWN("");

    companion object {
        fun valueOfIgnoreCase(name: String?): ImageRatio {
            return enumValues<ImageRatio>().firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: UNKNOWN
        }
    }
}
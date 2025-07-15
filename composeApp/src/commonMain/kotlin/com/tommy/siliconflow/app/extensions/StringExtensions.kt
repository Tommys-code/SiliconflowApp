package com.tommy.siliconflow.app.extensions

import com.tommy.siliconflow.app.utils.ImageProcessing

fun String.dealApikey(): String {
    if (length <= 8) return this
    return this.take(4) + "*".repeat(8) + takeLast(4)
}

fun formatPriceSimple(amount: Double): String {
    return if (amount % 1 == 0.0) {
        "¥ ${amount.toInt()}"
    } else {
        "¥ $amount"
    }
}

fun String.isValidUrl(): Boolean {
    return this.startsWith("http://") || this.startsWith("https://")
}

fun String.getUri(imageProcessing: ImageProcessing): String {
    if (isValidUrl()) {
        return this
    }
    return imageProcessing.getReferenceImageUri(this)
}

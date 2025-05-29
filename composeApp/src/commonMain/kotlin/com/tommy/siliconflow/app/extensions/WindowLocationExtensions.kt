package com.tommy.siliconflow.app.extensions

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun IntOffset.popupPosition(
    windowSize: IntSize,
    popupContentSize: IntSize,
    offset: IntOffset = IntOffset(0, 0),
): IntOffset {
    val x: Int = if (this.x + popupContentSize.width + offset.x > windowSize.width) {
        this.x - popupContentSize.width
    } else {
        this.x + offset.x
    }
    val y: Int = if (this.y + popupContentSize.height + offset.y > windowSize.height) {
        this.y - popupContentSize.height
    } else {
        this.y + offset.y
    }
    return IntOffset(x, y)
}

fun IntOffset.topPosition(
    popupContentSize: IntSize
): IntOffset {
    return IntOffset(this.x, y - popupContentSize.height)
}
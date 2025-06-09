package com.tommy.siliconflow.app.extensions

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

fun IntOffset.popupPosition(offset: IntOffset = IntOffset.Zero): (
    anchorBounds: IntRect,
    windowSize: IntSize,
    popupContentSize: IntSize,
) -> IntOffset = { _, windowSize, popupContentSize ->
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
    IntOffset(x, y)
}

val IntOffset.topPosition: (anchorBounds: IntRect, windowSize: IntSize, popupContentSize: IntSize) -> IntOffset
    get() = { _, _, popupContentSize ->
        IntOffset(this.x, this.y - popupContentSize.height)
    }


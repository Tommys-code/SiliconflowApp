package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.tommy.siliconflow.app.PlatformType
import com.tommy.siliconflow.app.getPlatform

private class DefaultPopupPositionProvider(
    private val topHeight: Int,
    val calculatePosition: (
        anchorBounds: IntRect,
        windowSize: IntSize,
        popupContentSize: IntSize
    ) -> IntOffset,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // iOS ignoresSafeArea will make popup y-axis offset
        val isIOS = getPlatform().type == PlatformType.IOS
        val offsetY = if (isIOS) {
            -topHeight
        } else {
            0
        }
        val pos = calculatePosition(anchorBounds, windowSize, popupContentSize)
        return pos.copy(y = pos.y + offsetY)
    }
}


@Composable
fun CustomPopup(
    onDismissRequest: (() -> Unit)? = null,
    focusable: Boolean = true,
    calculatePosition: ((
        anchorBounds: IntRect,
        windowSize: IntSize,
        popupContentSize: IntSize
    ) -> IntOffset)? = null,
    content: @Composable () -> Unit
) {
    calculatePosition?.let {
        val topHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = focusable),
            popupPositionProvider = DefaultPopupPositionProvider(topHeight, it),
            content = content,
        )
    } ?: run {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = focusable),
            content = content,
        )
    }
}


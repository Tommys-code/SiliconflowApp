package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.extensions.topPosition
import com.tommy.siliconflow.app.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_check

@Stable
data class ImageRatioPopupState(
    val offset: IntOffset = IntOffset(0, 0),
    val selected: ImageRatio,
)

@Composable
fun ImageRatioPopup(state: MutableState<ImageRatioPopupState?>) {
    state.value?.let {
        Popup(
            onDismissRequest = { state.value = null },
            properties = PopupProperties(focusable = true),
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    return it.offset.topPosition(popupContentSize)
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                    .width(150.dp)
                    .background(AppTheme.colorScheme.popContainer),
            ) {
                ImageRatio.entries.forEach { entry ->
                    RatioItem(entry, entry == it.selected) {}
                }
            }
        }
    }
}

@Composable
private fun RatioItem(ratio: ImageRatio, isSelected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(ratio.desc) },
        leadingIcon = {
            Box(modifier = Modifier.size(20.dp)) {
                Box(
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = AppTheme.colorScheme.iconContainer,
                        shape = RoundedCornerShape(4.dp),
                    ).aspectRatio(ratio.size)
                )
            }
        },
        trailingIcon = {
            if (isSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = "check",
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    )
}
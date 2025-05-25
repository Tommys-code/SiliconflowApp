package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.tommy.siliconflow.app.data.MainDialog
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.extensions.popupPosition
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.copy
import siliconflowapp.composeapp.generated.resources.copy_success
import siliconflowapp.composeapp.generated.resources.delete
import siliconflowapp.composeapp.generated.resources.ic_content_copy
import siliconflowapp.composeapp.generated.resources.ic_delete

enum class ChatType {
    SEND,
    RECEIVER,
    THINKING,
}

@Stable
data class ChatPopupState(
    val offset: IntOffset = IntOffset(0, 0),
    val history: ChatHistory,
    val type: ChatType,
) {
    val content: String?
        get() = when (type) {
            ChatType.THINKING -> history.thinking
            ChatType.SEND -> history.send?.content
            ChatType.RECEIVER -> history.receive?.content
        }
}

@Composable
fun ChatPopup(
    popupState: MutableState<ChatPopupState?>,
    doEvent: (MainViewEvent) -> Unit,
) {
    val state = popupState.value
    state?.let {
        val clipboard = LocalClipboardManager.current
        Popup(
            onDismissRequest = { popupState.value = null },
            properties = PopupProperties(focusable = true),
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    return it.offset.popupPosition(windowSize, popupContentSize)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                    .width(150.dp)
                    .background(AppTheme.colorScheme.popContainer),
            ) {
                ChatPopupMenuItem(stringResource(Res.string.copy), Res.drawable.ic_content_copy) {
                    it.content?.let { content ->
                        clipboard.setText(AnnotatedString(content))
                        doEvent(MainViewEvent.ShowToast(msgRes = Res.string.copy_success))
                    }
                    popupState.value = null
                }
                HorizontalDivider(thickness = 0.5.dp)
                ChatPopupMenuItem(stringResource(Res.string.delete), Res.drawable.ic_delete) {
                    doEvent(MainViewEvent.ShowOrHideDialog(MainDialog.DeleteChatHistory(it.history)))
                    popupState.value = null
                }
            }
        }
    }
}

@Composable
private fun ChatPopupMenuItem(
    text: String,
    leadingIcon: DrawableResource,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text) },
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = "icon",
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    )
}
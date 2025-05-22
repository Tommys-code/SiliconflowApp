package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.tommy.siliconflow.app.data.MainDialog
import com.tommy.siliconflow.app.data.SessionPopupState
import com.tommy.siliconflow.app.extensions.popupPosition
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.delete
import siliconflowapp.composeapp.generated.resources.edit_session_name
import siliconflowapp.composeapp.generated.resources.mul_select

@Composable
internal fun SessionPopup(
    popupState: StateFlow<SessionPopupState?>,
    doEvent: (MainViewEvent) -> Unit,
) {
    val state = popupState.collectAsState().value
    if (state != null) {
        Popup(
            onDismissRequest = { doEvent.invoke(MainViewEvent.DismissPopup) },
            properties = PopupProperties(focusable = true),
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    return state.offset.popupPosition(windowSize, popupContentSize)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                    .width(100.dp)
                    .background(Color.White)
                    .padding(vertical = 8.dp),
            ) {
                PopupItem(Res.string.mul_select, doEvent) {
                    doEvent.invoke(MainViewEvent.MultipleSelectionMode(true))
                }
                HorizontalDivider(
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                PopupItem(Res.string.edit_session_name, doEvent) {
                    doEvent.invoke(MainViewEvent.ShowOrHideDialog(MainDialog.EditSessionName(state.session)))
                }
                PopupItem(Res.string.delete, doEvent) {
                    doEvent.invoke(MainViewEvent.ShowOrHideDialog(MainDialog.DeleteSession(state.session)))
                }
            }
        }
    }
}

@Composable
private fun PopupItem(
    resource: StringResource,
    doEvent: (MainViewEvent) -> Unit,
    click: () -> Unit = {},
) {
    Text(
        stringResource(resource),
        modifier = Modifier.clickable {
            doEvent.invoke(MainViewEvent.DismissPopup)
            click()
        }.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp)
    )
}
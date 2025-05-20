package com.tommy.siliconflow.app.data

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset
import com.tommy.siliconflow.app.data.db.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewState {
    private val _drawerState = DrawerState(DrawerValue.Closed)
    val drawerState: DrawerState
        get() = _drawerState

    private val _popupState = MutableStateFlow<SessionPopupState?>(null)
    val popupState: StateFlow<SessionPopupState?> = _popupState

    private val _dialogState = MutableStateFlow<MainDialog?>(null)
    val dialogState: StateFlow<MainDialog?> = _dialogState

    fun toggleDrawer(scope: CoroutineScope) {
        scope.launch {
            drawerState.apply {
                if (isOpen) close() else open()
            }
        }
    }

    fun showPopup(session: Session, offset: IntOffset) {
        _popupState.value = SessionPopupState(session, offset)
    }

    fun dismissPopup() {
        _popupState.value = null
    }

    fun showOrHideDialog(dialog: MainDialog?) {
        _dialogState.value = dialog
    }
}

@Stable
data class SessionPopupState(
    val session: Session,
    val offset: IntOffset,
)

sealed class MainDialog {
    class EditSessionName(val session: Session) : MainDialog()
    class DeleteSession(val session: Session) : MainDialog()
}
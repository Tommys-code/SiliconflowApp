package com.tommy.siliconflow.app.data

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset
import com.tommy.siliconflow.app.data.db.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewState {
    private val _drawerState = DrawerState(DrawerValue.Closed)
    val drawerState: DrawerState
        get() = _drawerState

    private val _popupState = MutableStateFlow<SessionPopupState?>(null)
    val popupState: StateFlow<SessionPopupState?> = _popupState

    private val _dialogState = MutableStateFlow<MainDialog?>(null)
    val dialogState: StateFlow<MainDialog?> = _dialogState

    val popupSession: Flow<Session?> = popupState.map { it?.session }

    private val _selectSessions = MutableStateFlow<List<Session>?>(null)
    val selectSessions: StateFlow<List<Session>?> = _selectSessions

    fun toggleDrawer(scope: CoroutineScope) {
        multipleSelection(false)
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

    fun multipleSelection(open: Boolean) {
        _selectSessions.value = if (open) emptyList() else null
    }

    fun sessionCheck(session: Session) {
        _selectSessions.value?.let {
            if (it.contains(session)) {
                _selectSessions.value = it - session
            } else {
                _selectSessions.value = it + session
            }
        } ?: run {
            _selectSessions.value = listOf(session)
        }
    }

    fun checkAll(session: List<Session>) {
        _selectSessions.value?.let {
            if (session.size == it.size) {
                _selectSessions.value = emptyList()
            } else {
                _selectSessions.value = session
            }
        }
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
    class DeleteSessions(val sessions: List<Session>) : MainDialog()
}
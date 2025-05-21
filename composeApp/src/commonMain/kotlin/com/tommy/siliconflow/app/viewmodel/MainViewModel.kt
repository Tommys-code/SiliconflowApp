package com.tommy.siliconflow.app.viewmodel

import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.data.MainDialog
import com.tommy.siliconflow.app.data.MainViewState
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.repository.ChatRepository
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.delete_session_error
import siliconflowapp.composeapp.generated.resources.delete_session_success
import siliconflowapp.composeapp.generated.resources.edit_session_name_error
import siliconflowapp.composeapp.generated.resources.edit_session_name_success

sealed class MainViewEvent {
    data class Navigate(val route: String) : MainViewEvent()
    data class ShowToast(val msg: String? = null, val msgRes: StringResource? = null) : MainViewEvent()

    data class ToggleDrawer(val scope: CoroutineScope) : MainViewEvent()
    data class ChangeSession(val session: Session?) : MainViewEvent()
    data class ShowPopup(val session: Session, val offset: IntOffset) : MainViewEvent()
    data object DismissPopup : MainViewEvent()
    data class ShowOrHideDialog(val dialog: MainDialog?) : MainViewEvent()
    data class EditSessionName(val session: Session) : MainViewEvent()
    data class DeleteSession(val session: List<Session>) : MainViewEvent()
    data class MultipleSelectionMode(val open: Boolean) : MainViewEvent()
    data class SessionCheck(val session: Session) : MainViewEvent()
    data object CheckAll : MainViewEvent()
}

class MainViewModel(
    private val settingDataStore: SettingDataStore,
    private val siliconFlowRepository: SiliconFlowRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val userInfo = siliconFlowRepository.userInfo

    val mainViewState = MainViewState()

    private val _viewEvent = MutableSharedFlow<MainViewEvent>()
    val viewEvent: SharedFlow<MainViewEvent> = _viewEvent

    val sessionList = chatRepository.sessionList
    val currentSession = chatRepository.currentSession
    val chatHistory = chatRepository.chatHistory
    val answer = chatRepository.answer

    init {
        viewModelScope.launch {
            viewEvent.collect {
                when (it) {
                    is MainViewEvent.ToggleDrawer -> mainViewState.toggleDrawer(it.scope)
                    is MainViewEvent.ChangeSession -> chatRepository.changeSession(it.session)
                    is MainViewEvent.ShowPopup -> mainViewState.showPopup(it.session, it.offset)
                    MainViewEvent.DismissPopup -> mainViewState.dismissPopup()
                    is MainViewEvent.ShowOrHideDialog -> mainViewState.showOrHideDialog(it.dialog)
                    is MainViewEvent.EditSessionName -> {
                        val res = if (chatRepository.changeSessionName(it.session)) {
                            Res.string.edit_session_name_success
                        } else {
                            Res.string.edit_session_name_error
                        }
                        doEvent(MainViewEvent.ShowToast(msgRes = res))
                    }

                    is MainViewEvent.DeleteSession -> {
                        val res = if (chatRepository.deleteSession(it.session)) {
                            Res.string.delete_session_success
                        } else {
                            Res.string.delete_session_error
                        }
                        doEvent(MainViewEvent.ShowToast(msgRes = res))
                    }

                    is MainViewEvent.MultipleSelectionMode -> mainViewState.multipleSelection(it.open)
                    is MainViewEvent.SessionCheck -> mainViewState.sessionCheck(it.session)
                    MainViewEvent.CheckAll -> mainViewState.checkAll(sessionList.first())
                    else -> {}
                }
            }
        }
    }

    fun doEvent(event: MainViewEvent) {
        viewModelScope.launch { _viewEvent.emit(event) }
    }

    fun sendData(data: String) {
        viewModelScope.launch {
            chatRepository.sendData(data)
        }
    }

}
package com.tommy.siliconflow.app.viewmodel

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.repository.ChatRepository
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingDataStore: SettingDataStore,
    private val siliconFlowRepository: SiliconFlowRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val userInfo = siliconFlowRepository.userInfo

    private val _drawerState = DrawerState(DrawerValue.Closed)
    val drawerState: DrawerState
        get() = _drawerState

    val sessionList = chatRepository.sessionList
    val chatHistory = chatRepository.chatHistory
    val answer = chatRepository.answer

    fun toggleDrawer(scope: CoroutineScope) {
        scope.launch {
            drawerState.apply {
                if (isOpen) close() else open()
            }
        }
    }

    fun sendData(data: String) {
        viewModelScope.launch {
            chatRepository.sendData(data)
        }
    }

}
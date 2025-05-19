package com.tommy.siliconflow.app.viewmodel

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.lifecycle.ViewModel
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingDataStore: SettingDataStore,
    private val siliconFlowRepository: SiliconFlowRepository,
) : ViewModel() {

    val userInfo = siliconFlowRepository.userInfo

    private val _drawerState = DrawerState(DrawerValue.Closed)
    val drawerState: DrawerState
        get() = _drawerState

    fun toggleDrawer(scope: CoroutineScope) {
        scope.launch {
            drawerState.apply {
                if (isOpen) close() else open()
            }
        }
    }

}
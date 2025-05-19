package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    settingDataStore: SettingDataStore,
    private val siliconFlowRepository: SiliconFlowRepository,
) : ViewModel() {

    val apiKey = settingDataStore.getApiKey()
    val userInfo = siliconFlowRepository.userInfo

    fun login(key: String) {
        viewModelScope.launch { siliconFlowRepository.getUserInfo(key) }
    }

}
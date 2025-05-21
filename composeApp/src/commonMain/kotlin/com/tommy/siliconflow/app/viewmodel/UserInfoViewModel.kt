package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.extensions.dealApikey
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserInfoViewModel(
    private val siliconFlowRepository: SiliconFlowRepository
) : ViewModel() {

    val userInfo = siliconFlowRepository.userInfo
    val apiKey = siliconFlowRepository.apikey.map { it?.dealApikey() }

    private val _logoutResult = MutableStateFlow<Resource<Boolean>>(Resource.Loading(false))
    val logoutResult: StateFlow<Resource<Boolean>> = _logoutResult

    fun logout() {
        viewModelScope.launch {
            _logoutResult.emit(Resource.loading)
            siliconFlowRepository.logout()
            _logoutResult.emit(Resource.Success(true))
        }
    }

}
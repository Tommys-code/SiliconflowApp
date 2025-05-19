package com.tommy.siliconflow.app.repository

import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.data.UserInfo
import com.tommy.siliconflow.app.data.UserInfoResponse
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.network.error.ApiKeyEmptyException
import com.tommy.siliconflow.app.network.service.SiliconFlowService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first

class SiliconFlowRepository(
    private val service: SiliconFlowService,
    private val settingDataStore: SettingDataStore,
) {

    private val _userInfo = MutableStateFlow<Resource<UserInfo>>(Resource.loading)
    val userInfo: StateFlow<Resource<UserInfo>> = _userInfo

    suspend fun getUserInfo() {
        try {
            _userInfo.emit(Resource.loading)
            val apiKey = settingDataStore.getApiKey().conflate().first()
            if (apiKey.isNullOrBlank()) throw ApiKeyEmptyException
            val userInfo = service.getUserInfo(apiKey)
            saveUserInfo(userInfo)
            userInfo.data?.let { _userInfo.emit(Resource.Success(it)) } ?: run {
                _userInfo.emit(Resource.Error(Exception("empty data")))
            }
        } catch (e: Exception) {
            _userInfo.emit(Resource.Error(e))
        }
    }

    suspend fun getUserInfo(apiKey: String) {
        try {
            _userInfo.emit(Resource.loading)
            val userInfo = service.getUserInfo(apiKey)
            saveUserInfo(userInfo, apiKey)
            userInfo.data?.let {
                _userInfo.emit(Resource.Success(it))
            } ?: run {
                _userInfo.emit(Resource.Error(Exception("empty data")))
            }
        } catch (e: Exception) {
            _userInfo.emit(Resource.Error(e))
        }
    }

    private suspend fun saveUserInfo(userInfo: UserInfoResponse, apiKey: String? = null) {
        if (userInfo.status) {
            userInfo.data?.let {
                settingDataStore.saveUserInfo(it)
            }
            apiKey?.let { settingDataStore.saveApiKey(it) }
        }
    }

}
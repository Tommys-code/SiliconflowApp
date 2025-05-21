package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tommy.siliconflow.app.extensions.dealApikey
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.flow.map

class UserInfoViewModel(
    siliconFlowRepository: SiliconFlowRepository
) : ViewModel() {

    val userInfo = siliconFlowRepository.userInfo
    val apiKey = siliconFlowRepository.apikey.map { it?.dealApikey() }

}
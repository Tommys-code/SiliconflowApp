package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class SplashViewEvent {
    data class Navigate(val route: Any) : SplashViewEvent()
}

class SplashViewModel(private val repository: SiliconFlowRepository) : ViewModel() {

    private val _viewEvent = MutableSharedFlow<SplashViewEvent>()
    val viewEvent: SharedFlow<SplashViewEvent> = _viewEvent

    init {
        viewModelScope.launch {
            listOf(
                async { delay(1000) },
                async { repository.getUserInfo() }
            ).awaitAll()
            initNavigate()
        }
    }

    private fun initNavigate() {
        viewModelScope.launch {
            repository.userInfo.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        _viewEvent.emit(SplashViewEvent.Navigate(AppScreen.Login))
                    }

                    is Resource.Success -> {
                        _viewEvent.emit(SplashViewEvent.Navigate(AppScreen.Main))
                    }

                    else -> {

                    }
                }
            }
        }
    }
}
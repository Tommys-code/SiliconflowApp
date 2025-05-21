package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.navigation.Route
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class SplashViewEvent {
    data class Navigate(val route: String) : SplashViewEvent()
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

    private fun initNavigate(){
        viewModelScope.launch {
            repository.userInfo.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        _viewEvent.emit(SplashViewEvent.Navigate(Route.LOGIN_SCREEN))
//                        when (it.exception) {
//                            ApiKeyEmptyException -> _viewEvent.emit(SplashViewEvent.Navigate(Route.LOGIN_SCREEN))
//                            is AuthException -> _viewEvent.emit(SplashViewEvent.Navigate(Route.LOGIN_SCREEN))
//                        }
                    }

                    is Resource.Success -> {
                        _viewEvent.emit(SplashViewEvent.Navigate(Route.MAIN_SCREEN))
                    }

                    else -> {

                    }
                }
            }
        }
    }
}
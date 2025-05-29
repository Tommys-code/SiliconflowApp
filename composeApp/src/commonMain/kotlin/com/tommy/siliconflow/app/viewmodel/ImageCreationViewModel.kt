package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.repository.ImageCreationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

sealed class ImageCreationEvent {
    data class Creation(val prompt: String) : ImageCreationEvent()
}

class ImageCreationViewModel(
    private val repository: ImageCreationRepository,
    sessionID: Long?,
) : ViewModel() {

    private val _viewEvent = MutableSharedFlow<ImageCreationEvent>()
    val viewEvent: SharedFlow<ImageCreationEvent> = _viewEvent

    private val currentSession = MutableStateFlow<Session?>(null)
    private val _imageCreationData = MutableStateFlow(ImageCreationData(prompt = ""))
    val imageCreationData: StateFlow<ImageCreationData> = _imageCreationData

    private val _createResult = MutableStateFlow<Resource<Unit>>(Resource.init)
    val createResult: StateFlow<Resource<Unit>> = _createResult

    @OptIn(ExperimentalCoroutinesApi::class)
    val history: Flow<List<ImageCreationHistory>> = currentSession.flatMapLatest {
        it?.let { repository.getHistory(it.id) } ?: emptyFlow()
    }

    init {
        viewModelScope.launch {
            sessionID?.let { currentSession.value = repository.getSessionById(it) }
        }
        observerViewEvent()
    }

    private fun observerViewEvent() {
        viewModelScope.launch {
            viewEvent.collectLatest {
                when (it) {
                    is ImageCreationEvent.Creation -> createImage(it.prompt)
                }
            }
        }
    }

    private fun createImage(prompt: String) {
        viewModelScope.launch {
            _imageCreationData.value = _imageCreationData.value.copy(prompt = prompt)
            val id: Long = currentSession.value?.let {
                repository.insertHistory(it.id, imageCreationData.value)
            } ?: run {
                repository.createSession(_imageCreationData.value.prompt).let {
                    currentSession.value = it
                    repository.insertHistory(it.id, imageCreationData.value)
                }
            }
            runCatching {
                _createResult.value = Resource.loading
                repository.createImage(_imageCreationData.value)
            }.onSuccess {
                repository.updateHistory(id, it)
                _createResult.value = Resource.Success(Unit)
            }.onFailure {
                _createResult.value = Resource.Error(it)
            }
        }
    }

    fun doEvent(event: ImageCreationEvent) {
        viewModelScope.launch { _viewEvent.emit(event) }
    }

}
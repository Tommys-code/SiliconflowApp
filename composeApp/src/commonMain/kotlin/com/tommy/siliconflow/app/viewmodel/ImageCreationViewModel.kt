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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ImageCreationViewModel(
    private val repository: ImageCreationRepository,
    sessionID: Long?,
) : ViewModel() {

    private val currentSession = MutableStateFlow<Session?>(null)
    private val _imageCreationData = MutableStateFlow(ImageCreationData(prompt = ""))
    val imageCreationData: StateFlow<ImageCreationData> = _imageCreationData

    private val _createResult = MutableStateFlow<Resource<Unit>>(Resource.init)

    @OptIn(ExperimentalCoroutinesApi::class)
    val history: Flow<List<ImageCreationHistory>> = currentSession.flatMapLatest {
        it?.let { repository.getHistory(it.id) } ?: emptyFlow()
    }

    init {
        viewModelScope.launch {
            sessionID?.let { currentSession.value = repository.getSessionById(it) }
        }
    }

    fun sendDesc() {
        viewModelScope.launch {
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

}
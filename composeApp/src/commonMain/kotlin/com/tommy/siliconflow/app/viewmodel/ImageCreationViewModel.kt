package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.PlatformContext
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.ImageCreationDynamicData
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.extensions.generate
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.platform.ImageData
import com.tommy.siliconflow.app.repository.ImageCreationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.delete_error
import siliconflowapp.composeapp.generated.resources.delete_success

sealed class ImageCreationEvent {
    data class Navigate(val route: AppScreen) : ImageCreationEvent()
    data class Creation(val prompt: String, val context: PlatformContext) : ImageCreationEvent()
    data class UpdateRatio(val ratio: ImageRatio) : ImageCreationEvent()
    data class UpdateBatchSize(val size: Int) : ImageCreationEvent()
    data object ScrollTOBottom : ImageCreationEvent()
    data class ShowToast(val msg: StringResource) : ImageCreationEvent()
    data class DeleteHistory(val history: ImageCreationHistory) : ImageCreationEvent()
    data class UpdateReferenceImage(val imageData: ImageData?) : ImageCreationEvent()
}

class ImageCreationViewModel(
    private val repository: ImageCreationRepository,
    sessionID: Long?,
) : ViewModel() {

    private val _viewEvent = MutableSharedFlow<ImageCreationEvent>()
    val viewEvent: SharedFlow<ImageCreationEvent> = _viewEvent

    private val currentSession = MutableStateFlow<Session?>(null)

    private val _imageCreationDynamicData = MutableStateFlow(ImageCreationDynamicData())
    val imageCreationData: Flow<ImageCreationData> =
        repository.imageCreationBaseInfo.combine(_imageCreationDynamicData) { base, dynamic ->
            ImageCreationData(dynamic, base)
        }

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
                    is ImageCreationEvent.Creation -> createImage(it.prompt, it.context)
                    is ImageCreationEvent.UpdateRatio -> updateRatio(it.ratio)
                    is ImageCreationEvent.UpdateBatchSize -> updateBatchSize(it.size)
                    is ImageCreationEvent.DeleteHistory -> {
                        val msg = if (repository.deleteHistory(it.history)) {
                            Res.string.delete_success
                        } else {
                            Res.string.delete_error
                        }
                        doEvent(ImageCreationEvent.ShowToast(msg))
                    }

                    is ImageCreationEvent.UpdateReferenceImage -> updateReferenceImage(it.imageData)
                    ImageCreationEvent.ScrollTOBottom,
                    is ImageCreationEvent.Navigate,
                    is ImageCreationEvent.ShowToast -> {
                    }
                }
            }
        }
    }

    private fun createImage(prompt: String, context: PlatformContext) {
        viewModelScope.launch {
            val data = _imageCreationDynamicData.value.copy(prompt = prompt).generate(context)
            val id: Long = currentSession.value?.let {
                repository.insertHistory(it.id, data)
            } ?: run {
                repository.createSession(prompt).let {
                    currentSession.value = it
                    repository.insertHistory(it.id, data)
                }
            }
            runCatching {
                _createResult.value = Resource.loading
                repository.createImage(data)
            }.onSuccess {
                repository.updateHistory(id, it)
                _createResult.value = Resource.Success(Unit)
            }.onFailure {
                _createResult.value = Resource.Error(it)
            }
            clearDynamicData()
        }
    }

    fun doEvent(event: ImageCreationEvent) {
        viewModelScope.launch { _viewEvent.emit(event) }
    }

    private fun updateRatio(ratio: ImageRatio) {
        viewModelScope.launch {
            repository.saveImageCreationData(
                imageCreationData.conflate().first().baseInfo.copy(imageRadio = ratio)
            )
        }
    }

    private fun updateBatchSize(size: Int) {
        viewModelScope.launch {
            repository.saveImageCreationData(
                imageCreationData.conflate().first().baseInfo.copy(batchSize = size)
            )
        }
    }

    private fun updateReferenceImage(imageData: ImageData?) {
        _imageCreationDynamicData.value =
            _imageCreationDynamicData.value.copy(referenceImage = imageData)
    }

    private fun clearDynamicData() {
        _imageCreationDynamicData.value = ImageCreationDynamicData()
    }

}
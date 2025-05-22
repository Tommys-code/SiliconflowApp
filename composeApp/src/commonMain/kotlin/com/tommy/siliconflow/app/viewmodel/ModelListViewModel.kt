package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.datasbase.ModelStore
import com.tommy.siliconflow.app.model.LocalAIModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ModelListViewModel(private val modelStore: ModelStore) : ViewModel() {
    val modelList = modelStore.modelList
    val currentModel = modelStore.currentModel

    fun changeModel(model: LocalAIModel) {
        viewModelScope.launch {
            if (model == currentModel.first()) return@launch
            modelStore.changeModel(model)
        }
    }
}
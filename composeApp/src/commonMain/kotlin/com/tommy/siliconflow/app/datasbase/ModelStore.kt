package com.tommy.siliconflow.app.datasbase

import com.tommy.siliconflow.app.model.LocalAIModel
import com.tommy.siliconflow.app.model.LocalAITextModel
import com.tommy.siliconflow.app.model.TextAIModel
import com.tommy.siliconflow.app.model.toLocalAIModel
import com.tommy.siliconflow.app.network.JsonSerializationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import siliconflowapp.composeapp.generated.resources.Res

interface ModelStore {
    val currentModel: Flow<LocalAIModel>
    val modelList: Flow<List<LocalAIModel>>

    suspend fun changeModel(model: LocalAIModel)
}

class ModelStoreImpl(
    private val settingDataStore: SettingDataStore,
    scope: CoroutineScope
) : ModelStore {

    private val textAIModelList = MutableStateFlow<List<LocalAIModel>>(emptyList())

    override val currentModel =
        settingDataStore.getCurrentModel().combine(textAIModelList) { name, models ->
            models.firstOrNull { it.model == name } ?: models.first()
        }

    override val modelList = textAIModelList

    init {
        scope.launch {
            Res.readBytes("files/text_model.json").decodeToString().let { data ->
                JsonSerializationHelper.jsonX().decodeFromString<List<TextAIModel>>(data).map {
                    it.toLocalAIModel()
                }
            }.let {
                textAIModelList.value = it
            }
        }
    }

    override suspend fun changeModel(model: LocalAIModel) {
        settingDataStore.chooseModel(model.model)
    }
}